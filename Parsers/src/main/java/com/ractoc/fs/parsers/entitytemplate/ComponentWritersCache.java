package com.ractoc.fs.parsers.entitytemplate;

import com.ractoc.fs.parsers.ParserException;
import com.ractoc.fs.es.EntityComponent;
import com.ractoc.fs.parsers.entitytemplate.annotation.Template;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

public class ComponentWritersCache {

    private static Map<Class<? extends EntityComponent>, ComponentWriter> writers =
            new HashMap<Class<? extends EntityComponent>, ComponentWriter>();
    private ClassLoader loader = this.getClass().getClassLoader();;

    public ComponentWriter getWriterForComponentType(Class<? extends EntityComponent> componentType) {
        if (testComponentTypeRegistered(componentType)) {
            return writers.get(componentType);
        } else {
            ComponentWriter writer = determineWriter(componentType);
            return writer;
        }
    }

    private boolean testComponentTypeRegistered(Class<? extends EntityComponent> componentType) {
        return writers.containsKey(componentType);
    }

    private ComponentWriter determineWriter(Class<? extends EntityComponent> componentType) {
        try {
            ComponentWriter writerInstance = createWriterInstanceForComponentType(componentType);
            writers.put(componentType, writerInstance);
            return writerInstance;
        } catch (Exception ex) {
            throw new ParserException("Unable to instantiate writer.", ex);
        }
    }

    private String getComponentTypeWriter(Class<? extends EntityComponent> componentType) {
        Template templateAnnotation = componentType.getAnnotation(Template.class);
        return templateAnnotation.writer();
    }

    private ComponentWriter createWriterInstanceForComponentType(Class<? extends EntityComponent> componentType) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        String writer = getComponentTypeWriter(componentType);
        Class<ComponentWriter> writerClass = (Class<ComponentWriter>) loader.loadClass(writer);
        ComponentWriter writerInstance = writerClass.newInstance();
        return writerInstance;
    }

    void setClassLoader(URLClassLoader loader) {
        this.loader = loader;
    }
}

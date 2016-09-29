package com.ractoc.fs.parsers.entitytemplate;

import com.ractoc.fs.parsers.ParserException;
import com.ractoc.fs.es.EntityComponent;
import com.ractoc.fs.parsers.entitytemplate.annotation.Template;
import java.util.*;

public class ComponentParsersCache {

    private static Map<Class<? extends EntityComponent>, ComponentParser> parsers =
            new HashMap<Class<? extends EntityComponent>, ComponentParser>();
    private ClassLoader classLoader;

    public ComponentParser getParserForComponentType(Class<? extends EntityComponent> componentType) {
        if (testComponentTypeRegistered(componentType)) {
            return parsers.get(componentType);
        } else {
            ComponentParser parser = determineParser(componentType);
            return parser;
        }
    }

    private boolean testComponentTypeRegistered(Class<? extends EntityComponent> componentType) {
        return parsers.containsKey(componentType);
    }

    private ComponentParser determineParser(Class<? extends EntityComponent> componentType) {
        try {
            ComponentParser parserInstance = createParserInstanceForComponentType(componentType);
            parsers.put(componentType, parserInstance);
            return parserInstance;
        } catch (Exception ex) {
            throw new ParserException("Unable to instantiate parser for " + componentType, ex);
        }
    }

    private String getComponentTypeParser(Class<? extends EntityComponent> componentType) {
        Template templateAnnotation = componentType.getAnnotation(Template.class);
        if (templateAnnotation == null) {
            throw new ParserException("Component Type not intended for use in Entity Templates.");
        }
        return templateAnnotation.parser();
    }

    private ComponentParser createParserInstanceForComponentType(Class<? extends EntityComponent> componentType) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        if (classLoader == null) {
            classLoader = getClass().getClassLoader();
        }
        String parser = getComponentTypeParser(componentType);
        Class<ComponentParser> parserClass = (Class<ComponentParser>) classLoader.loadClass(parser);
        ComponentParser parserInstance = parserClass.newInstance();
        return parserInstance;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}

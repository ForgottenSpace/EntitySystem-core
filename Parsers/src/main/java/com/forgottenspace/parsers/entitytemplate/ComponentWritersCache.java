package com.forgottenspace.parsers.entitytemplate;

import com.forgottenspace.es.EntityComponent;
import com.forgottenspace.parsers.ParserException;
import com.forgottenspace.parsers.entitytemplate.annotation.Template;

import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

public class ComponentWritersCache {

	private static Map<Class<? extends EntityComponent>, ComponentWriter> writers = new HashMap<>();
	private ClassLoader loader = this.getClass().getClassLoader();

	public ComponentWriter getWriterForComponentType(Class<? extends EntityComponent> componentType) {
		if (testComponentTypeRegistered(componentType)) {
			return writers.get(componentType);
		} else {
			return determineWriter(componentType);
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

	@SuppressWarnings("unchecked")
	private ComponentWriter createWriterInstanceForComponentType(Class<? extends EntityComponent> componentType)
			throws ClassNotFoundException, IllegalAccessException, InstantiationException {
		String writer = getComponentTypeWriter(componentType);
		Class<ComponentWriter> writerClass = (Class<ComponentWriter>) loader.loadClass(writer);
		return writerClass.newInstance();
	}

	void setClassLoader(URLClassLoader loader) {
		this.loader = loader;
	}
}

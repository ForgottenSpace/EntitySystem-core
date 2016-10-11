package com.forgottenspace.parsers.entitytemplate;

import com.forgottenspace.es.EntityComponent;
import com.forgottenspace.parsers.ParserException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ComponentWriter {

    protected Map<String, String> properties = new HashMap<>();
    protected final List<String> mandatoryProperties = new ArrayList<>();

    public Map<String, String> getPropertiesFromComponent(EntityComponent component) {
        try {
            pullPropertiesFromComponent(component);
        } catch (ClassCastException ex) {
            throw new ParserException("Invalid component type for parser.", ex);
        }
        return properties;
    }

    protected abstract void pullPropertiesFromComponent(EntityComponent component);

    protected String extractValueWithMethod(EntityComponent component, String methodName) {
        try {
            return component.getClass().getMethod(methodName).invoke(component).toString();
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new ParserException("unable to call " + methodName + " on class " + component.getClass().getName(), ex);
        }
    }
}

package com.ractoc.fs.parsers.entitytemplate;

import com.ractoc.fs.parsers.ParserException;
import com.ractoc.fs.es.EntityComponent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ComponentWriter {

    protected Map<String, String> properties = new HashMap<String, String>();
    protected final List<String> mandatoryProperties = new ArrayList<String>();

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
        } catch (NoSuchMethodException ex) {
            throw new ParserException("unable to call " + methodName + " on class " + component.getClass().getName(), ex);
        } catch (SecurityException ex) {
            throw new ParserException("unable to call " + methodName + " on class " + component.getClass().getName(), ex);
        } catch (IllegalAccessException ex) {
            throw new ParserException("unable to call " + methodName + " on class " + component.getClass().getName(), ex);
        } catch (IllegalArgumentException ex) {
            throw new ParserException("unable to call " + methodName + " on class " + component.getClass().getName(), ex);
        } catch (InvocationTargetException ex) {
            throw new ParserException("unable to call " + methodName + " on class " + component.getClass().getName(), ex);
        }
    }
}

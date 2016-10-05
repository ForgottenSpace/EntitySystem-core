package com.forgottenspace.ai;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.forgottenspace.es.Entity;

public abstract class AiComponent implements Cloneable {

    private final String id;
    protected Map<String, Object> props;
    protected Map<String, Object> exits;
    protected Map<String, Object> initPros;
    protected AiScript aiScript;
    protected Entity entity;

    public AiComponent(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public final void configure(Map<String, Object> props, Map<String, Object> exits) {
        checkExits(exits);
        this.props = props;
        this.exits = exits;
    }

    private void checkProps() {
        Set<String> propKeys = new HashSet<>(props.keySet());
        propKeys.addAll(aiScript.getGlobalProps().keySet());
        if (!propKeys.containsAll(Arrays.asList(getMandatoryProperties()))) {
            throw new AiException("Not all mandatory properties set.");
        }
    }

    private void checkExits(Map<String, Object> exits) {
        if (!exits.keySet().containsAll(Arrays.asList(getMandatoryExits()))) {
            throw new AiException("Not all mandatory properties set.");
        }
    }

    protected Object getProp(String key) {
        if (props.containsKey(key)) {
            return props.get(key);
        } else if (aiScript.getGlobalProps().containsKey(key)) {
            return aiScript.getGlobalProp(key);
        } else {
            throw new AiException("No such property: " + key);
        }
    }

    public abstract String[] getMandatoryProperties();

    public abstract String[] getMandatoryExits();

    public abstract void initialiseProperties();

    public abstract void updateProperties();

    public final void initialise(Entity entity, AiScript aiScript) {
    	this.entity = entity;
        this.aiScript = aiScript;
        checkProps();
        initialiseProperties();
    }

    public void update(float tpf) {
    }

    public static AiComponent copyComponent(AiComponent component) {
    	AiComponent copy = null;
        try {
        	copy = component.getClass().newInstance();
        	copy.configure(component.props, component.exits);
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new AiException("Unable to clone AI component.", ex);
        }
        return copy;
     }

}
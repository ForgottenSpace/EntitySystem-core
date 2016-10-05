package com.forgottenspace.ai;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.forgottenspace.es.Entities;
import com.forgottenspace.es.Entity;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;

public class AiScript implements Savable {

    private static final String EXIT = "exit";
	private String name;
    private String entry;
    private Entity entity;
    private final Map<String, AiComponent> components = new HashMap<String, AiComponent>();
    private AiComponent currentComponent;
    private Map<String, Object> globalProps = new HashMap<String, Object>();
    private boolean subScript;
    private boolean finished = false;

    public AiScript(String name) {
        this(name, false);
    }

    public AiScript(String name, boolean subScript) {
        this.name = name;
        this.subScript = subScript;
    }

    public void addComponent(AiComponent component) {
        components.put(component.getId(), component);
    }

    public AiComponent getComponent(String alias) {
        return components.get(alias);
    }

    public Collection<AiComponent> getComponents() {
        return components.values();
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    public void initialise(Entity entity) {
        this.entity = entity;
        this.finished = false;
        setCurrentComponent(entry);
    }

    public void update(float tpf) {
        currentComponent.update(tpf);
    }

    public void setCurrentComponent(String componentName) {
        if (componentName.equalsIgnoreCase(EXIT)) {
            if (subScript) {
                finished = true;
            } else {
                Entities.getInstance().destroyEntity(entity);
            }
        } else {
            currentComponent = components.get(componentName);
            currentComponent.initialise(entity, this);
        }
    }

    public void setGlobalProp(String key, Object value) {
        globalProps.put(key, value);
    }

    public Object getGlobalProp(String key) {
        return globalProps.get(key);
    }

    public Map<String, Object> getGlobalProps() {
        return globalProps;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setSubScript(boolean subScript) {
        this.subScript = subScript;
    }

    public String getName() {
        return name;
    }

    public void write(JmeExporter ex) throws IOException {
        OutputCapsule capsule = ex.getCapsule(this);
        capsule.write(name, "name", null);
    }

    public void read(JmeImporter im) throws IOException {
        InputCapsule capsule = im.getCapsule(this);
        name = capsule.readString("name", null);
    }

    public String getEntry() {
        return entry;
    }
}

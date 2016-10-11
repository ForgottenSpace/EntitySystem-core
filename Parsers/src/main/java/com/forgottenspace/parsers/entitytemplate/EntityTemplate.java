package com.forgottenspace.parsers.entitytemplate;

import com.forgottenspace.es.EntityComponent;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EntityTemplate implements Savable {

    private String name;
    private List<EntityComponent> components = new ArrayList<>();

    public EntityTemplate(String name) {
        this.name = name;
    }

    public void addComponent(EntityComponent component) {
        if (component == null) {
            throw new NullPointerException();
        }
        components.add(component);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<EntityComponent> getComponents() {
        return components;
    }

    public EntityComponent[] getComponentsAsArray() {
        return components.toArray(new EntityComponent[]{});
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule capsule = ex.getCapsule(this);
        capsule.write(name, "name", null);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule capsule = im.getCapsule(this);
        name = capsule.readString("name", null);
    }

    // TODO: figure out why this method is needed and how it exactly works
    public void removeComponent(EntityComponent componentToRemove) {
        List<EntityComponent> componentsToRemove = new ArrayList<>();
        for (EntityComponent currentComponent : components) {
            if (currentComponent.getClass().getName().equals(componentToRemove.getClass().getName())) {
                componentsToRemove.add(currentComponent);
            }
        }
        components.removeAll(componentsToRemove);
    }
}

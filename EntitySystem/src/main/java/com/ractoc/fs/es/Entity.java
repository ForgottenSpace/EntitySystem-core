package com.ractoc.fs.es;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Entity {

    private final Long id;
    private final Set<Class<? extends EntityComponent>> componentTypes;

    public Entity(Long id, Class<? extends EntityComponent>... componentTypes) {
        if (IsEmpty.componentTypes(componentTypes)) {
            throw new EntityException("Either no component types are supplied or some of the supplied component types are null.");
        }
        this.id = id;
        this.componentTypes = new HashSet<Class<? extends EntityComponent>>(Arrays.asList(componentTypes));
    }

    public boolean matches(ComponentTypeCriteria criteria) {
        return criteria.matches(componentTypes);
    }

    public Long getId() {
        return id;
    }

    public Set<Class<? extends EntityComponent>> getComponentTypes() {
        return componentTypes;
    }

    public void addComponentType(Class<? extends EntityComponent> componentType) {
        componentTypes.add(componentType);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof Entity && id == ((Entity) obj).id;
    }

    @Override
    public String toString() {
        return "Entity{" + "id=" + id + ", componentTypes=" + componentTypes + '}';
    }


}

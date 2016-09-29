package com.ractoc.fs.es;

import java.util.HashMap;
import java.util.Map;

final class ComponentStorages {

    private Map<Class<? extends EntityComponent>, ComponentStorage> registeredTypesWithStorage = new HashMap<Class<? extends EntityComponent>, ComponentStorage>();

    public void registerComponentTypeWithComponentStorage(Class<? extends EntityComponent> componentType, ComponentStorage componentStorage) {
        if (testComponentTypeRegistered(componentType)) {
            throw new StorageException("ComponentType already registered.");
        } else {
            registeredTypesWithStorage.put(componentType, componentStorage);
        }
    }
    
    public void unregisterComponentTypeWithComponentStorage(Class<? extends EntityComponent> componentType) {
        registeredTypesWithStorage.remove(componentType);
    }

    private boolean testComponentTypeRegistered(Class<? extends EntityComponent> componentType) {
        return registeredTypesWithStorage.containsKey(componentType);
    }

    public void storeComponentForEntity(Long entityId, EntityComponent entityComponent) {
        if (testComponentTypeRegistered(entityComponent.getClass())) {
            ComponentStorage componentStorage = getComponentStorage(entityComponent.getClass());
            componentStorage.storeComponentForEntity(entityId, entityComponent);
        } else {
            componentTypeNotRegistered(entityComponent.getClass());
        }
    }

    public void changeComponentForEntity(Long entityId, EntityComponent entityComponent) {
        if (testComponentTypeRegistered(entityComponent.getClass())) {
            ComponentStorage componentStorage = getComponentStorage(entityComponent.getClass());
            componentStorage.changeComponentForEntity(entityId, entityComponent);
        } else {
            componentTypeNotRegistered(entityComponent.getClass());
        }
    }

    @SuppressWarnings("unchecked")
	public <T extends EntityComponent> T loadComponentForEntity(Long entityId, Class<T> componentType) {
        T component = null;
        if (testComponentTypeRegistered(componentType)) {
            ComponentStorage componentStorage = getComponentStorage(componentType);
            component = (T) componentStorage.loadComponentForEntity(entityId);
        } else {
            componentTypeNotRegistered(componentType);
        }
        return component;
    }

    public void removeComponentForEntity(Long entityId, Class<? extends EntityComponent> componentType) {
        if (testComponentTypeRegistered(componentType)) {
            ComponentStorage componentStorage = getComponentStorage(componentType);
            componentStorage.removeComponentForEntity(entityId);
        } else {
            componentTypeNotRegistered(componentType);
        }
    }

    private ComponentStorage getComponentStorage(Class<? extends EntityComponent> componentType) {
        return (ComponentStorage) registeredTypesWithStorage.get(componentType);
    }

    private void componentTypeNotRegistered(Class<? extends EntityComponent> componentType) throws StorageException {
        throw new StorageException("ComponentType not registered: " + componentType);
    }


}

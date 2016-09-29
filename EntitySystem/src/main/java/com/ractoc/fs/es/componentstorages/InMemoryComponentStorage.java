package com.ractoc.fs.es.componentstorages;

import com.ractoc.fs.es.ComponentStorage;
import com.ractoc.fs.es.EntityComponent;
import com.ractoc.fs.es.StorageException;
import java.util.HashMap;
import java.util.Map;

public class InMemoryComponentStorage implements ComponentStorage {
    
    private Map<Long, EntityComponent> componentsByEntity = new HashMap<Long, EntityComponent>();

    @Override
    public void storeComponentForEntity(Long entityId, EntityComponent entityComponent) {
        if (testComponentAlreadyStoredForEntity(entityId)) {
            throw new StorageException("Component already stored for entity.");
        } else {
            componentsByEntity.put(entityId, entityComponent);
        }
    }

    @Override
    public EntityComponent loadComponentForEntity(Long entityId) {
        EntityComponent component = null;
        if (testComponentAlreadyStoredForEntity(entityId)) {
            component = componentsByEntity.get(entityId);
        } else {
            componentNotStoredForEntity();
        }
        return component;
    }

    @Override
    public void changeComponentForEntity(Long entityId, EntityComponent entityComponent) {
        if (testComponentAlreadyStoredForEntity(entityId)) {
            componentsByEntity.put(entityId, entityComponent);
        } else {
            componentNotStoredForEntity();
        }
    }

    @Override
    public void removeComponentForEntity(Long entityId) {
        if (testComponentAlreadyStoredForEntity(entityId)) {
            componentsByEntity.remove(entityId);
        } else {
            componentNotStoredForEntity();
        }
    }

    private boolean testComponentAlreadyStoredForEntity(Long entityId) {
        return componentsByEntity.containsKey(entityId);
    }

    private void componentNotStoredForEntity() throws StorageException {
        throw new StorageException("Component not stored for entity.");
    }

}

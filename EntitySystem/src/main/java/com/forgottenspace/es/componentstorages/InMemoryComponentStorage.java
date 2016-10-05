package com.forgottenspace.es.componentstorages;

import java.util.HashMap;
import java.util.Map;

import com.forgottenspace.es.ComponentStorage;
import com.forgottenspace.es.EntityComponent;
import com.forgottenspace.es.StorageException;

public class InMemoryComponentStorage implements ComponentStorage {
    
    private Map<Long, EntityComponent> componentsByEntity = new HashMap<Long, EntityComponent>();

    public void storeComponentForEntity(Long entityId, EntityComponent entityComponent) {
        if (isComponentAlreadyStoredForEntity(entityId)) {
            componentAlreadyStoredForEntity();
        } else {
            componentsByEntity.put(entityId, entityComponent);
        }
    }

    public EntityComponent loadComponentForEntity(Long entityId) {
        EntityComponent component = null;
        if (isComponentAlreadyStoredForEntity(entityId)) {
            component = componentsByEntity.get(entityId);
        } else {
            componentNotStoredForEntity();
        }
        return component;
    }

    public void changeComponentForEntity(Long entityId, EntityComponent entityComponent) {
        if (isComponentAlreadyStoredForEntity(entityId)) {
            componentsByEntity.put(entityId, entityComponent);
        } else {
            componentNotStoredForEntity();
        }
    }

    public void removeComponentForEntity(Long entityId) {
        if (isComponentAlreadyStoredForEntity(entityId)) {
            componentsByEntity.remove(entityId);
        } else {
            componentNotStoredForEntity();
        }
    }

    private boolean isComponentAlreadyStoredForEntity(Long entityId) {
        return componentsByEntity.containsKey(entityId);
    }

    private void componentAlreadyStoredForEntity() {
        throw new StorageException("Component already stored for entity.");
    }

    private void componentNotStoredForEntity() {
        throw new StorageException("Component not stored for entity.");
    }

}

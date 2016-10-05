package com.forgottenspace.es;

import com.forgottenspace.es.ComponentStorage;
import com.forgottenspace.es.EntityComponent;
import com.forgottenspace.es.StorageException;

public class MockComponentStorage implements ComponentStorage {

    public void storeComponentForEntity(Long entityId, EntityComponent entityComponent) {
    }

    public EntityComponent loadComponentForEntity(Long entityId) {
        if (entityId.equals(new Long(1l))) {
            throw new StorageException("No component for entityId " + entityId);
        }
        return new MockEntityComponent();
    }

    public void changeComponentForEntity(Long entityId, EntityComponent entityComponent) {
    }

    public void removeComponentForEntity(Long entityId) {
        if (entityId.equals(new Long(1l))) {
            throw new StorageException("No component for entityId " + entityId);
        }
    }

}

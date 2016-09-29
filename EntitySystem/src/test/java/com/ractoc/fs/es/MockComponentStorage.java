package com.ractoc.fs.es;

public class MockComponentStorage implements ComponentStorage {

    @Override
    public void storeComponentForEntity(Long entityId, EntityComponent entityComponent) {
    }

    @Override
    public EntityComponent loadComponentForEntity(Long entityId) {
        if (entityId.equals(new Long(1l))) {
            throw new StorageException("No component for entityId " + entityId);
        }
        return new MockEntityComponent();
    }

    @Override
    public void changeComponentForEntity(Long entityId, EntityComponent entityComponent) {
    }

    @Override
    public void removeComponentForEntity(Long entityId) {
        if (entityId.equals(new Long(1l))) {
            throw new StorageException("No component for entityId " + entityId);
        }
    }

}

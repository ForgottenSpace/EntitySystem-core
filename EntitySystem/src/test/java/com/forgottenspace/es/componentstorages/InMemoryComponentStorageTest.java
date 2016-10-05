package com.forgottenspace.es.componentstorages;

import org.junit.Test;

import com.forgottenspace.es.EntityComponent;
import com.forgottenspace.es.MockEntityComponent;
import com.forgottenspace.es.StorageException;

public class InMemoryComponentStorageTest {
    
    private InMemoryComponentStorage inMemoryComponentStorage = new InMemoryComponentStorage();
    private Long entityId = 1l;
    private EntityComponent entityComponent = new MockEntityComponent();
    
    public InMemoryComponentStorageTest() {
    }
    
    @Test(expected=StorageException.class)
    public void storeComponentTwice() {
        inMemoryComponentStorage.storeComponentForEntity(entityId, entityComponent);
        inMemoryComponentStorage.storeComponentForEntity(entityId, entityComponent);
    }
    
    @Test(expected=StorageException.class)
    public void changeComponentEntityIdNotFound() {
        inMemoryComponentStorage.changeComponentForEntity(entityId, entityComponent);
    }
    
    @Test
    public void changeComponent() {
        inMemoryComponentStorage.storeComponentForEntity(entityId, entityComponent);
        inMemoryComponentStorage.changeComponentForEntity(entityId, entityComponent);
    }
    
    @Test(expected=StorageException.class)
    public void loadComponentEntityNotFound() {
        inMemoryComponentStorage.loadComponentForEntity(entityId);
    }
    
    @Test
    public void loadComponent() {
        inMemoryComponentStorage.storeComponentForEntity(entityId, entityComponent);
        inMemoryComponentStorage.loadComponentForEntity(entityId);
    }
    
    @Test(expected=StorageException.class)
    public void removeComponentEntityNotFound() {
        inMemoryComponentStorage.removeComponentForEntity(entityId);
    }
    
    @Test
    public void removeComponent() {
        inMemoryComponentStorage.storeComponentForEntity(entityId, entityComponent);
        inMemoryComponentStorage.removeComponentForEntity(entityId);
    }
}

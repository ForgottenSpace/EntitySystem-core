package com.forgottenspace.es;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.forgottenspace.es.ComponentStorage;
import com.forgottenspace.es.ComponentStorages;
import com.forgottenspace.es.EntityComponent;
import com.forgottenspace.es.StorageException;
import com.forgottenspace.es.componentstorages.InMemoryComponentStorage;

public class ComponentStoragesTest {

    private ComponentStorage componentStorage = new InMemoryComponentStorage();
    private ComponentStorages componentStorages = new ComponentStorages();
    private Long entityIdFailure = 1l;
    private Long entityIdSuccess = 2l;
    private EntityComponent entityComponent = new MockEntityComponent();

    @Test(expected = StorageException.class)
    public void registerComponentTypeWithComponentStorageTwice() {
        componentStorages.registerComponentTypeWithComponentStorage(MockEntityComponent.class, componentStorage);
        componentStorages.registerComponentTypeWithComponentStorage(MockEntityComponent.class, componentStorage);
    }
    
    @Test(expected = StorageException.class) 
    public void storeComponentForEntityComponentTypeNotRegistered() {
        componentStorages.storeComponentForEntity(entityIdFailure, entityComponent);
    }
    
    @Test
    public void storeComponentForEntity() {
        componentStorages.registerComponentTypeWithComponentStorage(MockEntityComponent.class, componentStorage);
        componentStorages.storeComponentForEntity(2L, entityComponent);
    }
    
    @Test(expected = StorageException.class) 
    public void changeComponentForEntityComponentTypeNotRegistered() {
        componentStorages.changeComponentForEntity(entityIdFailure, entityComponent);
    }
    
    @Test
    public void changeComponentForEntity() {
        componentStorages.registerComponentTypeWithComponentStorage(MockEntityComponent.class, componentStorage);
        componentStorages.storeComponentForEntity(entityIdSuccess, entityComponent);
        componentStorages.changeComponentForEntity(entityIdSuccess, entityComponent);
    }
    
    @Test(expected = StorageException.class)
    public void loadComponentForEntityIdComponentTypeNotRegistered() {
        componentStorages.loadComponentForEntity(entityIdFailure, MockEntityComponent.class);
    }
    
    @Test(expected = StorageException.class)
    public void loadComponentForEntityNoComponentForEntity() {
        componentStorages.registerComponentTypeWithComponentStorage(MockEntityComponent.class, componentStorage);
        componentStorages.loadComponentForEntity(entityIdFailure, MockEntityComponent.class);
    }
    
    @Test
    public void loadComponentForEntity() {
        componentStorages.registerComponentTypeWithComponentStorage(MockEntityComponent.class, componentStorage);
        componentStorages.storeComponentForEntity(entityIdSuccess, entityComponent);
        EntityComponent ec = componentStorages.loadComponentForEntity(entityIdSuccess, MockEntityComponent.class);
        assertEquals(entityComponent, ec);
    }
    
    @Test(expected = StorageException.class)
    public void removeComponentForEntityIdComponentTypeNotRegistered() {
        componentStorages.removeComponentForEntity(entityIdFailure, MockEntityComponent.class);
    }
    
    @Test(expected = StorageException.class)
    public void removeComponentForEntityNoComponentForEntity() {
        componentStorages.registerComponentTypeWithComponentStorage(MockEntityComponent.class, componentStorage);
        componentStorages.removeComponentForEntity(entityIdFailure, MockEntityComponent.class);
    }
    
    @Test(expected=StorageException.class)
    public void removeComponentForEntity() {
        componentStorages.registerComponentTypeWithComponentStorage(MockEntityComponent.class, componentStorage);
        componentStorages.storeComponentForEntity(entityIdSuccess, entityComponent);
        EntityComponent ec = componentStorages.loadComponentForEntity(entityIdSuccess, MockEntityComponent.class);
        assertEquals(entityComponent, ec);
        componentStorages.removeComponentForEntity(entityIdSuccess, MockEntityComponent.class);
        ec = componentStorages.loadComponentForEntity(entityIdSuccess, MockEntityComponent.class);
    }
}

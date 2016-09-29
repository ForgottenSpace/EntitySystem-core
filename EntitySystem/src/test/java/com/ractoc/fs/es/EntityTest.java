/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ractoc.fs.es;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ractoc
 */
public class EntityTest {
    
    private Long entityId = 1L;
    
    public EntityTest() {
    }
    
    @Test(expected=EntityException.class)
    public void instantiateNoComponentTypes() {
        new Entity(entityId);
    }
    
    @Test(expected=EntityException.class)
    public void instantiateEmptyComponentType() {
        new Entity(entityId, MockEntityComponent.class, null);
    }
    
    @Test
    public void entityHashCode() {
        Entity e = new Entity(entityId, MockEntityComponent.class);
        assertEquals(entityId, new Long(e.hashCode()));
    }
    
    @Test
    public void entityEqualsEmpty() {
        Entity e = new Entity(entityId, MockEntityComponent.class);
        assertFalse(e.equals(null));
    }
    
    @Test
    public void entityEqualsWrongObject() {
        Entity e = new Entity(entityId, MockEntityComponent.class);
        assertFalse(e.equals("This should not be a String object."));
    }
    
    @Test
    public void entityEqualsInvalidEntity() {
        Entity e = new Entity(entityId, MockEntityComponent.class);
        assertFalse(e.equals(new Entity(2L, MockEntityComponent.class)));
    }
    
    @Test
    public void entityEquals() {
        Entity e = new Entity(entityId, MockEntityComponent.class);
        assertTrue(e.equals(new Entity(entityId, MockEntityComponent.class)));
    }
}

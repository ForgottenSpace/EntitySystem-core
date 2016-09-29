package com.ractoc.fs.es;

import org.junit.Test;
import static org.junit.Assert.*;

public class EntityResultSetTest {

    private ComponentTypeCriteria ctc = new ComponentTypeCriteria(MockEntityComponent.class);
    private Long entityId = 1L;

    public EntityResultSetTest() {
    }

    @Test
    public void add() {
        EntityResultSet ers = createEntityResultSetAndAddCorrectly();
        createEntityResultSetAndAddCorrectly();
        Entity ent = new Entity(entityId, MockEntityComponent.class);
        EntityResultSet.UpdateProcessor updProc = ers.getUpdateProcessor();
        assertFalse(updProc.getAddedEntities().isEmpty());
        assertTrue(updProc.getChangedEntities().isEmpty());
        assertTrue(updProc.getRemovedEntities().isEmpty());
        assertEquals(ent, updProc.getAddedEntities().get(0));
        assertEquals(0, ers.size());
        updProc.finalizeUpdates();
        assertEquals(1, ers.size());
    }

    @Test
    public void addNoMatch() {
        EntityResultSet esr = new EntityResultSet(ctc);
        boolean addResult = esr.add(new Entity(entityId, MatchEntityComponent.class));
        assertFalse(addResult);
    }

    @Test
    public void addAlreadyAdded() {
        EntityResultSet ers = createEntityResultSetAndAddCorrectly();
        Entity ent = new Entity(entityId, MockEntityComponent.class);
        boolean addResult = ers.add(ent);
        assertTrue(addResult);
        EntityResultSet.UpdateProcessor updProc = ers.getUpdateProcessor();
        assertFalse(updProc.getAddedEntities().isEmpty());
        assertTrue(updProc.getChangedEntities().isEmpty());
        assertTrue(updProc.getRemovedEntities().isEmpty());
        assertEquals(ent, updProc.getAddedEntities().get(0));
        assertEquals(0, ers.size());
        updProc.finalizeUpdates();
        assertEquals(1, ers.size());
    }

    @Test
    public void removeTwoTransactions() {
        EntityResultSet ers = createEntityResultSetAndAddCorrectly();
        EntityResultSet.UpdateProcessor updProc = ers.getUpdateProcessor();
        updProc.finalizeUpdates();
        Entity ent = new Entity(entityId, SecondMockEntityComponent.class);
        boolean removeResult = ers.remove(ent);
        assertTrue(removeResult);
        updProc = ers.getUpdateProcessor();
        assertTrue(updProc.getAddedEntities().isEmpty());
        assertTrue(updProc.getChangedEntities().isEmpty());
        assertFalse(updProc.getRemovedEntities().isEmpty());
        assertEquals(ent, updProc.getRemovedEntities().get(0));
        assertEquals(1, ers.size());
        updProc.finalizeUpdates();
        assertEquals(0, ers.size());
    }

    @Test
    public void removeWithinTransaction() {
        EntityResultSet ers = createEntityResultSetAndAddCorrectly();
        Entity ent = new Entity(entityId, SecondMockEntityComponent.class);
        boolean removeResult = ers.remove(ent);
        assertTrue(removeResult);
        EntityResultSet.UpdateProcessor updProc = ers.getUpdateProcessor();
        assertFalse(updProc.getAddedEntities().isEmpty());
        assertTrue(updProc.getChangedEntities().isEmpty());
        assertFalse(updProc.getRemovedEntities().isEmpty());
        assertEquals(ent, updProc.getAddedEntities().get(0));
        assertEquals(ent, updProc.getRemovedEntities().get(0));
        assertEquals(0, ers.size());
        updProc.finalizeUpdates();
        assertEquals(0, ers.size());
    }

    @Test
    public void removeNoEntity() {
        EntityResultSet ers = new EntityResultSet(ctc);
        boolean removeResult = ers.remove(new Entity(entityId, MockEntityComponent.class));
        assertFalse(removeResult);
    }

    @Test(expected=ClassCastException.class)
    public void removeInvalidType() {
        EntityResultSet ers = new EntityResultSet(ctc);
        boolean removeResult = ers.remove("This is not a valid entity object");
        assertFalse(removeResult);
    }

    @Test
    public void changeNoMatch() {
        EntityResultSet esr = new EntityResultSet(ctc);
        boolean changeResult = esr.change(new Entity(entityId, MatchEntityComponent.class));
        assertFalse(changeResult);
    }

    @Test
    public void changeNoEntity() {
        EntityResultSet ers = new EntityResultSet(ctc);
        boolean changeResult = ers.change(new Entity(entityId, MockEntityComponent.class));
        assertTrue(changeResult);
        EntityResultSet.UpdateProcessor updProc =  ers.getUpdateProcessor();
        assertTrue(updProc.getChangedEntities().isEmpty());
        assertFalse(updProc.getAddedEntities().isEmpty());
    }

    @Test
    public void changeTwoTransactions() {
        EntityResultSet ers = createEntityResultSetAndAddCorrectly();
        EntityResultSet.UpdateProcessor updProc = ers.getUpdateProcessor();
        updProc.finalizeUpdates();
        Entity ent = new Entity(entityId, MockEntityComponent.class);
        boolean changeResult = ers.change(ent);
        assertTrue(changeResult);
        updProc = ers.getUpdateProcessor();
        assertTrue(updProc.getAddedEntities().isEmpty());
        assertFalse(updProc.getChangedEntities().isEmpty());
        assertTrue(updProc.getRemovedEntities().isEmpty());
        assertEquals(ent, updProc.getChangedEntities().get(0));
        assertEquals(1, ers.size());
        updProc.finalizeUpdates();
        assertEquals(1, ers.size());
    }

    public void changeWithinTransaction() {
        EntityResultSet ers = createEntityResultSetAndAddCorrectly();
        Entity ent = new Entity(entityId, MockEntityComponent.class);
        boolean changeResult = ers.remove(ent);
        assertTrue(changeResult);
        EntityResultSet.UpdateProcessor updProc = ers.getUpdateProcessor();
        assertFalse(updProc.getAddedEntities().isEmpty());
        assertFalse(updProc.getChangedEntities().isEmpty());
        assertTrue(updProc.getRemovedEntities().isEmpty());
        assertEquals(ent, updProc.getAddedEntities().get(0));
        assertEquals(ent, updProc.getChangedEntities().get(0));
        assertEquals(0, ers.size());
        updProc.finalizeUpdates();
        assertEquals(1, ers.size());
    }

    private EntityResultSet createEntityResultSetAndAddCorrectly() {
        EntityResultSet ers = new EntityResultSet(ctc);
        boolean addResult = ers.add(new Entity(entityId, MockEntityComponent.class));
        assertTrue(addResult);
        return ers;
    }

    private class MatchEntityComponent implements EntityComponent {
    }
}

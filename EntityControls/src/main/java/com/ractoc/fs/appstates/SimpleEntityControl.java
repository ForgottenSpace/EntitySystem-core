package com.ractoc.fs.appstates;

import com.ractoc.fs.es.Entity;
import com.ractoc.fs.es.EntityComponent;
import com.ractoc.fs.es.EntityResultSet;

public abstract class SimpleEntityControl extends AbstractEntityControl {
    private EntityResultSet resultSet;
    
    public SimpleEntityControl(Class<EntityComponent>... componentTypes) {
        resultSet = queryEntityResultSet(componentTypes);
    }

    /**
     * Loops through a single resultset. This resultset is based on the component types supplied in the constructor.
     * First, the updateAddedEntity method is called once for each new entity to the resultset.
     * Then the updateChangedEntity method is called once for each changed entity in the resultset.
     * Finally, the updateRemovedEntity method is called once for each entity removed from the resultset.
     * @param tpf The time since the last call to the update method.
     */
    @Override
    public void update(float tpf) {
        super.update(tpf);
        EntityResultSet.UpdateProcessor updateProcessor = resultSet.getUpdateProcessor();
        for (Entity entity : updateProcessor.getAddedEntities()) {
            updateAddedEntity(entity, tpf);
        }
        for (Entity entity : updateProcessor.getChangedEntities()) {
            updateChangedEntity(entity, tpf);
        }
        for (Entity entity : updateProcessor.getRemovedEntities()) {
            updateRemovedEntity(entity, tpf);
        }
        updateProcessor.finalizeUpdates();
    }
    
    /**
     * Update the supplied added entity.
     * @param entity The entity that has been added to the resultset.
     * @param tpf The time since the last call to the update method.
     */
    public abstract void updateAddedEntity(Entity entity, float tpf);
    
    /**
     * Update the supplied changed entity.
     * @param entity The entity that has been changed 1n the resultset.
     * @param tpf The time since the last call to the update method.
     */
    public abstract void updateChangedEntity(Entity entity, float tpf);
    
    /**
     * Update the supplied removed entity.
     * @param entity The entity that has been removed from the resultset.
     * @param tpf The time since the last call to the update method.
     */
    public abstract void updateRemovedEntity(Entity entity, float tpf);
}

package com.ractoc.fs.es;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EntityResultSet extends AbstractSet<Entity> {

    private final ComponentTypeCriteria criteria;
    private final List<Entity> entities;
    private final List<Entity> addedEntities;
    private final List<Entity> removedEntities;
    private final List<Entity> changedEntities;

    public EntityResultSet(ComponentTypeCriteria criteria) {
        this.criteria = criteria;
        entities = new ArrayList<Entity>();
        addedEntities = new ArrayList<Entity>();
        removedEntities = new ArrayList<Entity>();
        changedEntities = new ArrayList<Entity>();
    }

    @Override
    public Iterator<Entity> iterator() {
        return entities.iterator();
    }

    @Override
    public int size() {
        return entities.size();
    }

    @Override
    public boolean add(Entity e) {
        if (canBeAdded(e)) {
            if (addedEntities.contains(e)) {
                return true;
            } else {
                return addedEntities.add(e);
            }
        } else {
            return false;
        }
    }

    private boolean canBeAdded(Entity e) {
        return !entities.contains(e) && e.matches(criteria);
    }

    @Override
    public boolean remove(Object o) {
        if (canBeRemoved((Entity) o)) {
            if (removedEntities.contains((Entity) o)) {
                return true;
            } else {
                return removedEntities.add((Entity) o);
            }
        } else {
            return false;
        }
    }

    private boolean canBeRemoved(Entity o) {
        return o instanceof Entity && (entities.contains(o) || addedEntities.contains(o)) && !((Entity) o).matches(criteria);
    }

    public boolean change(Entity entity) {
        if (canBeChanged(entity)) {
            if (changedEntities.contains(entity)) {
                return true;
            } else {
                return changedEntities.add(entity);
            }
        } else {
            return add(entity);
        }
    }

    private boolean canBeChanged(Entity e) {
        return (entities.contains(e) || addedEntities.contains(e)) && e.matches(criteria);
    }

    public UpdateProcessor getUpdateProcessor() {
        return new UpdateProcessor(this);
    }

    public class UpdateProcessor {

        private EntityResultSet entityResultSet;
        private List<Entity> addedEntities = new ArrayList<Entity>();
        private List<Entity> changedEntities = new ArrayList<Entity>();
        private List<Entity> removedEntities = new ArrayList<Entity>();

        private UpdateProcessor(EntityResultSet ers) {
            this.entityResultSet = ers;
            this.addedEntities.addAll(ers.addedEntities);
            this.changedEntities.addAll(ers.changedEntities);
            this.removedEntities.addAll(ers.removedEntities);
        }

        public List<Entity> getAddedEntities() {
            return this.addedEntities;
        }

        public List<Entity> getChangedEntities() {
            return this.changedEntities;
        }

        public List<Entity> getRemovedEntities() {
            return this.removedEntities;
        }

        public void finalizeUpdates() {
            entityResultSet.entities.addAll(this.addedEntities);
            entityResultSet.entities.removeAll(this.removedEntities);
            entityResultSet.addedEntities.removeAll(this.addedEntities);
            entityResultSet.changedEntities.removeAll(this.changedEntities);
            entityResultSet.removedEntities.removeAll(this.removedEntities);

            this.addedEntities = new ArrayList<Entity>();
            this.changedEntities = new ArrayList<Entity>();
            this.removedEntities = new ArrayList<Entity>();
        }
    }
}

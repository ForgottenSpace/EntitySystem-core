package com.forgottenspace.es;

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
        entities = new ArrayList<>();
        addedEntities = new ArrayList<>();
        removedEntities = new ArrayList<>();
        changedEntities = new ArrayList<>();
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
        return (entities.contains(o) || addedEntities.contains(o)) && !(o).matches(criteria);
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
    
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((criteria == null) ? 0 : criteria.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		EntityResultSet other = (EntityResultSet) obj;
		if (criteria == null) {
			if (other.criteria != null) {
				return false;
			}
		} else if (criteria.size() != other.criteria.size() || !criteria.containsAll(other.criteria)) {
			return false;
		}
		return true;
	}

	public UpdateProcessor getUpdateProcessor() {
        return new UpdateProcessor(this);
    }

    public class UpdateProcessor {

        private EntityResultSet entityResultSet;
        private List<Entity> addedEntities = new ArrayList<>();
        private List<Entity> changedEntities = new ArrayList<>();
        private List<Entity> removedEntities = new ArrayList<>();

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

            this.addedEntities = new ArrayList<>();
            this.changedEntities = new ArrayList<>();
            this.removedEntities = new ArrayList<>();
        }
    }
}

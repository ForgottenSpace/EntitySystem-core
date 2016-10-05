package com.forgottenspace.es;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class Entities {

    private static AtomicLong lastEntityId = new AtomicLong(0);
    private ComponentStorages componentStorages;
    private List<EntityResultSet> resultSets = new ArrayList<>();
    private static Entities instance = new Entities();
    private Map<Long, Entity> currentEntities = new HashMap<>();

    private Entities() {
        componentStorages = new ComponentStorages();
    }

    public static Entities getInstance() {
        return instance;
    }

    @SuppressWarnings("unchecked")
	public void registerComponentTypesWithComponentStorage(ComponentStorage componentStorage, Class<? extends EntityComponent>... componentTypes) {
        if (IsEmpty.componentTypes(componentTypes)) {
            noComponentTypeSupplied();
        } else {
            for (Class<? extends EntityComponent> componentType : componentTypes) {
                componentStorages.registerComponentTypeWithComponentStorage(componentType, componentStorage);
            }
        }
    }

    @SuppressWarnings("unchecked")
	public void unregisterComponentTypesWithComponentStorage(Class<? extends EntityComponent>... componentTypes) {
        if (IsEmpty.componentTypes(componentTypes)) {
            noComponentTypeSupplied();
        } else {
            for (Class<? extends EntityComponent> componentType : componentTypes) {
                componentStorages.unregisterComponentTypeWithComponentStorage(componentType);
            }
        }
    }

    public Entity createEntity(EntityComponent... components) {
        Entity entity = null;
        if (IsEmpty.components(components)) {
            noComponentSupplied();
        } else {
            entity = createEntityAddComponents(components);
            currentEntities.put(entity.getId(), entity);
        }
        return entity;
    }

    private Entity createEntityAddComponents(EntityComponent[] components) {
        Entity entity = null;
        if (IsEmpty.components(components)) {
            noComponentSupplied();
        } else {
        	Long entityId = getNextEntityId();
            entity = createEntity(entityId, components);
	        for (EntityComponent component : components) {
	            componentStorages.storeComponentForEntity(entityId, component);
	        }
        }
        return entity;
    }

    private long getNextEntityId() {
        return lastEntityId.incrementAndGet();
    }

    private Entity createEntity(Long entityId, EntityComponent[] components) {
        Entity entity = new Entity(entityId, convertComponentsToTypes(components));
        addToResultSets(entity);
        return entity;
    }

    public void addComponentsToEntity(Entity entity, EntityComponent... components) {
        addComponentsToEntity(components, entity);
        changeResultSets(entity);
    }

    private void addComponentsToEntity(EntityComponent[] components, Entity entity) {
        if (IsEmpty.components(components)) {
            noComponentSupplied();
        }
        for (EntityComponent component : components) {
            componentStorages.storeComponentForEntity(entity.getId(), component);
            entity.addComponentType(component.getClass());
        }
    }

    public void changeComponentsForEntity(Entity entity, EntityComponent... components) {
        removeComponentsFromEntity(entity, convertComponentsToTypes(components));
        addComponentsToEntity(components, entity);
        changeResultSets(entity);
    }

    public void removeComponentsFromEntity(Entity entity, EntityComponent... components) {
        if (IsEmpty.components(components)) {
            noComponentSupplied();
        }
        removeComponentsFromEntity(entity, convertComponentsToTypes(components));
        removeFromResultSets(entity);
    }

    @SuppressWarnings("unchecked")
	public void destroyEntity(Entity entity) {
        removeComponentsFromEntity(entity, entity.getComponentTypes().toArray(new Class[]{}));
        removeFromResultSets(entity);
        currentEntities.remove(entity.getId());
    }

    @SuppressWarnings("unchecked")
	private void removeComponentsFromEntity(Entity entity, Class<? extends EntityComponent>... componentTypes) {
        for (Class<? extends EntityComponent> componentType : componentTypes) {
            componentStorages.removeComponentForEntity(entity.getId(), componentType);
            entity.getComponentTypes().remove(componentType);
        }
    }

    private void addToResultSets(Entity entity) {
        for (EntityResultSet ers : resultSets) {
            ers.add(entity);
        }
    }

    private void removeFromResultSets(Entity entity) {
        for (EntityResultSet resultSet : resultSets) {
            resultSet.remove(entity);
        }
    }

    private void changeResultSets(Entity entity) {
        for (EntityResultSet resultSet : resultSets) {
            resultSet.change(entity);
        }
    }

    public <T extends EntityComponent> T loadComponentForEntity(Entity entity, Class<T> componentType) {
        return componentStorages.loadComponentForEntity(entity.getId(), componentType);
    }

    public EntityResultSet queryEntities(ComponentTypeCriteria criteria) {
        EntityResultSet resultSet = new EntityResultSet(criteria);
        resultSets.add(resultSet);
        return resultSet;
    }

    public void closeResultSet(EntityResultSet resultSet) {
        if (resultSets.contains(resultSet)) {
            resultSets.remove(resultSet);
        } else {
            throw new EntityException("ResultSet already closed.");
        }
    }

    protected void closeAllResultSets() {
        resultSets.clear();
    }

    private void noComponentTypeSupplied() {
        throw new EntityException("Either no component types are supplied or some of the supplied component types are null.");
    }

    private void noComponentSupplied() {
        throw new EntityException("Either no components are supplied or some of the supplied components are null.");
    }

    @SuppressWarnings("unchecked")
	private Class<? extends EntityComponent>[] convertComponentsToTypes(EntityComponent[] components) {
        Class<? extends EntityComponent>[] componentTypes = new Class[components.length];
        for (int index = 0; index < components.length; index++) {
            componentTypes[index] = components[index].getClass();
        }
        return componentTypes;
    }

    public Entity getEntityById(Long entityId) {
        return currentEntities.get(entityId);
    }
}

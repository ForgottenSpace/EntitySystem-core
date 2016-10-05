package com.ractoc.fs.components.ai;

import static com.ractoc.fs.components.ai.AiConstants.BOOM_EXIT;
import static com.ractoc.fs.components.ai.AiConstants.SHIP_ENTITY_PROPERTY;

import java.util.List;

import com.forgottenspace.ai.AiComponent;
import com.forgottenspace.es.ComponentTypeCriteria;
import com.forgottenspace.es.Entities;
import com.forgottenspace.es.Entity;
import com.forgottenspace.es.EntityResultSet;
import com.forgottenspace.es.components.ControlledComponent;
import com.forgottenspace.es.components.LocationComponent;
import com.forgottenspace.es.components.ShootMainComponent;
import com.forgottenspace.parsers.ai.AiComponentExit;

public class ShootComponent extends AiComponent {

    private static final Entities entities = Entities.getInstance();
	private Entity controlledEntity;
    private EntityResultSet controlledResultSet;
    @AiComponentExit(name = BOOM_EXIT, displayName = "Boom", type = String.class, shortDescription = "The player has been destroyed.")
    private String boom;

    public ShootComponent(String id) {
        super(id);
        queryControlledResultSet();
    }

	private void queryControlledResultSet() {
        ComponentTypeCriteria criteria = new ComponentTypeCriteria(LocationComponent.class, ControlledComponent.class);
        controlledResultSet = entities.queryEntities(criteria);
    }

    @Override
    public String[] getMandatoryProperties() {
        return new String[]{};
    }

    @Override
    public String[] getMandatoryExits() {
        return new String[]{BOOM_EXIT};
    }

    @Override
    public void initialiseProperties() {
        boom = (String) exits.get(BOOM_EXIT);
    }

    @Override
    public void updateProperties() {
    	// properties can not be updated
    }

    @Override
	public void update(float tpf) {
        determineControlledEntity();
        Entity shipEntity = entities.getEntityById((Long) getProp(SHIP_ENTITY_PROPERTY));
        if (shipEntity != null) {
            LocationComponent shipLocationComponent = entities.loadComponentForEntity(shipEntity, LocationComponent.class);
            if (controlledEntity != null && shipLocationComponent != null) {
                if (!shipEntity.matches(new ComponentTypeCriteria(ShootMainComponent.class))) {
                    entities.addComponentsToEntity(shipEntity, new ShootMainComponent(0f));
                }
            } else {
                entities.removeComponentsFromEntity(shipEntity, new ShootMainComponent(0f));
            }
        } else {
            aiScript.setCurrentComponent(boom);
        }
    }

    private void determineControlledEntity() {
        EntityResultSet.UpdateProcessor updateProcessor = controlledResultSet.getUpdateProcessor();
        updateRemovedEntities(updateProcessor.getRemovedEntities());
        updateAddedEntities(updateProcessor.getAddedEntities());
        updateProcessor.finalizeUpdates();
    }

    private void updateRemovedEntities(List<Entity> removedEntities) {
        if (!removedEntities.isEmpty()) {
            controlledEntity = null;
        }
    }

    private void updateAddedEntities(List<Entity> addedEntities) {
        if (!addedEntities.isEmpty()) {
            controlledEntity = addedEntities.get(0);
        }
    }
}

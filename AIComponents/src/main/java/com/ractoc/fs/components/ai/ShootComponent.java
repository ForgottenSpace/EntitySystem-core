package com.ractoc.fs.components.ai;

import com.jme3.asset.AssetManager;
import com.ractoc.fs.ai.AiComponent;
import com.ractoc.fs.ai.AiScript;
import com.ractoc.fs.components.es.ControlledComponent;
import com.ractoc.fs.components.es.LocationComponent;
import com.ractoc.fs.components.es.ShootMainComponent;
import com.ractoc.fs.es.ComponentTypeCriteria;
import com.ractoc.fs.es.Entities;
import com.ractoc.fs.es.Entity;
import com.ractoc.fs.es.EntityResultSet;
import com.ractoc.fs.parsers.ai.AiComponentExit;
import com.ractoc.fs.parsers.ai.AiComponentProperty;
import java.util.List;

public class ShootComponent extends AiComponent {

    private Entity controlledEntity;
    private Entity shipEntity;
    private EntityResultSet controlledResultSet;
    @AiComponentExit(name = "boom", displayName = "Boom", type = String.class, shortDescription = "The player has been destroyed.")
    private String boom;

    public ShootComponent(String id) {
        super(id);
        queryControlledResultSet();
    }

    private void queryControlledResultSet() {
        ComponentTypeCriteria criteria = new ComponentTypeCriteria(LocationComponent.class, ControlledComponent.class);
        controlledResultSet = Entities.getInstance().queryEntities(criteria);
    }

    @Override
    public String[] getMandatoryProperties() {
        return new String[]{};
    }

    @Override
    public String[] getMandatoryExits() {
        return new String[]{"boom"};
    }

    @Override
    public void initialiseProperties() {
        boom = (String) exits.get("boom");
    }

    @Override
    public void updateProperties() {
    }

    @Override
    public void update(float tpf) {
        determineControlledEntity();
        shipEntity = Entities.getInstance().getEntityById((Long) getProp("shipEntity"));
        if (shipEntity != null) {
            LocationComponent shipLocationComponent = Entities.getInstance().loadComponentForEntity(shipEntity, LocationComponent.class);
            if (controlledEntity != null && shipLocationComponent != null) {
                if (!shipEntity.matches(new ComponentTypeCriteria(ShootMainComponent.class))) {
                    Entities.getInstance().addComponentsToEntity(shipEntity, new ShootMainComponent(0f));
                }
            } else {
                Entities.getInstance().removeComponentsFromEntity(shipEntity, new ShootMainComponent(0f));
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
        if (removedEntities.size() > 0) {
            controlledEntity = null;
        }
    }

    private void updateAddedEntities(List<Entity> addedEntities) {
        if (addedEntities.size() > 0) {
            controlledEntity = addedEntities.get(0);
        }
    }
}

package com.ractoc.fs.components.ai;

import com.ractoc.fs.ai.AiComponent;
import com.ractoc.fs.components.es.ControlledComponent;
import com.ractoc.fs.components.es.LocationComponent;
import com.ractoc.fs.es.ComponentTypeCriteria;
import com.ractoc.fs.es.Entities;
import com.ractoc.fs.es.Entity;
import com.ractoc.fs.es.EntityResultSet;
import com.ractoc.fs.parsers.ai.AiComponentExit;
import com.ractoc.fs.parsers.ai.AiComponentProperty;
import java.util.List;

public class RangeTriggerComponent extends AiComponent {

    private EntityResultSet controlledResultSet;
    private Entity controlledEntity;
    @AiComponentProperty(name = "range", displayName = "Range", type = Float.class, shortDescription = "Range to execute the trigger and proceed to the next Ai Component")
    private Float range;
    @AiComponentExit(name = "inRange", displayName = "In Range", type = String.class, shortDescription = "The player is in range.")
    private String inRange;

    public RangeTriggerComponent(String id) {
        super(id);
        queryControlledResultSet();
    }

    private void queryControlledResultSet() {
        ComponentTypeCriteria criteria = new ComponentTypeCriteria(LocationComponent.class, ControlledComponent.class);
        controlledResultSet = Entities.getInstance().queryEntities(criteria);
    }

    @Override
    public String[] getMandatoryProperties() {
        return new String[]{"range"};
    }

    @Override
    public String[] getMandatoryExits() {
        return new String[]{"inRange"};
    }

    @Override
    public void initialiseProperties() {
        range = Float.valueOf((String) getProp("range"));
        inRange = (String) exits.get("inRange");
    }

    @Override
    public void updateProperties() {
        props.put("range", range.toString());
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        determineControlledEntity();
        LocationComponent tlc = Entities.getInstance().loadComponentForEntity(entity, LocationComponent.class);
        if (controlledEntity != null && tlc != null) {
            LocationComponent clc = Entities.getInstance().loadComponentForEntity(controlledEntity, LocationComponent.class);
            if (clc.getTranslation().distance(tlc.getTranslation()) <= range) {
                aiScript.setCurrentComponent(inRange);
            }
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

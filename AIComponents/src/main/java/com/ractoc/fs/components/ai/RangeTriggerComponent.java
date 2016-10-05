package com.ractoc.fs.components.ai;

import static com.ractoc.fs.components.ai.AiConstants.IN_RANGE_EXIT;
import static com.ractoc.fs.components.ai.AiConstants.APPROACH_RANGE_PROPERTY;

import java.util.List;

import com.forgottenspace.ai.AiComponent;
import com.forgottenspace.es.ComponentTypeCriteria;
import com.forgottenspace.es.Entities;
import com.forgottenspace.es.Entity;
import com.forgottenspace.es.EntityResultSet;
import com.forgottenspace.es.components.ControlledComponent;
import com.forgottenspace.es.components.LocationComponent;
import com.forgottenspace.parsers.ai.AiComponentExit;
import com.forgottenspace.parsers.ai.AiComponentProperty;

public class RangeTriggerComponent extends AiComponent {

	private EntityResultSet controlledResultSet;
    private Entity controlledEntity;
    @AiComponentProperty(name = APPROACH_RANGE_PROPERTY, displayName = "Range", type = Float.class, shortDescription = "Range to execute the trigger and proceed to the next Ai Component")
    private Float range;
    @AiComponentExit(name = IN_RANGE_EXIT, displayName = "In Range", type = String.class, shortDescription = "The player is in range.")
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
        return new String[]{APPROACH_RANGE_PROPERTY};
    }

    @Override
    public String[] getMandatoryExits() {
        return new String[]{IN_RANGE_EXIT};
    }

    @Override
    public void initialiseProperties() {
        range = Float.valueOf((String) getProp(APPROACH_RANGE_PROPERTY));
        inRange = (String) exits.get(IN_RANGE_EXIT);
    }

    @Override
    public void updateProperties() {
        props.put(APPROACH_RANGE_PROPERTY, range.toString());
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

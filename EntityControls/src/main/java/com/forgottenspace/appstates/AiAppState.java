package com.forgottenspace.appstates;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.forgottenspace.ai.AiScript;
import com.forgottenspace.es.Entities;
import com.forgottenspace.es.Entity;
import com.forgottenspace.es.EntityResultSet;
import com.forgottenspace.es.components.AiComponent;
import com.forgottenspace.es.components.LocationComponent;

public class AiAppState extends AbstractEntityControl {

    private EntityResultSet aiResultSet;
    private Map<Entity, AiScript> aiEntities = new HashMap<Entity, AiScript>();

    @SuppressWarnings("unchecked")
	public AiAppState() {
        aiResultSet = queryEntityResultSet(LocationComponent.class, AiComponent.class);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        determineAiEntities();

        for (AiScript aiScript : aiEntities.values()) {
            aiScript.update(tpf);
        }
    }

    private void determineAiEntities() {
        EntityResultSet.UpdateProcessor updateProcessor = aiResultSet.getUpdateProcessor();
        updateRemovedEntities(updateProcessor.getRemovedEntities());
        updateAddedEntities(updateProcessor.getAddedEntities());
        updateProcessor.finalizeUpdates();
    }

    private void updateRemovedEntities(List<Entity> removedEntities) {
        for (Entity entity : removedEntities) {
            aiEntities.remove(entity);
        }
    }

    private void updateAddedEntities(List<Entity> addedEntities) {
        for (Entity entity : addedEntities) {
            AiComponent aiComponent = Entities.getInstance().loadComponentForEntity(entity, AiComponent.class);
            AiScript aiScript = (AiScript) getAssetManager().loadAsset(aiComponent.getScript());
            aiScript.initialise(entity);
            aiScript.getGlobalProps().put("assetManager", getAssetManager());
            aiEntities.put(entity, aiScript);
        }
    }
}

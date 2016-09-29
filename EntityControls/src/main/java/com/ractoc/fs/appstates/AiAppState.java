package com.ractoc.fs.appstates;

import com.ractoc.fs.ai.AiScript;
import com.ractoc.fs.components.es.AiComponent;
import com.ractoc.fs.components.es.LocationComponent;
import com.ractoc.fs.es.Entities;
import com.ractoc.fs.es.Entity;
import com.ractoc.fs.es.EntityResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AiAppState extends AbstractEntityControl {

    private EntityResultSet aiResultSet;
    private Map<Entity, AiScript> aiEntities = new HashMap<Entity, AiScript>();

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
            aiScript.initialise(entity, getAssetManager());
            aiEntities.put(entity, aiScript);
        }
    }
}

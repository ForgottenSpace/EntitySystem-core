package com.ractoc.fs.components.ai;

import static com.ractoc.fs.components.ai.AiConstants.ASSET_MANAGER_PROPERTY;
import static com.ractoc.fs.components.ai.AiConstants.SHIP_ENTITY_PROPERTY;
import static com.ractoc.fs.components.ai.AiConstants.SHIP_TEMPLATE_PROPERTY;
import static com.ractoc.fs.components.ai.AiConstants.SPAWNED_EXIT;

import com.forgottenspace.ai.AiComponent;
import com.forgottenspace.es.Entities;
import com.forgottenspace.es.Entity;
import com.forgottenspace.es.components.LocationComponent;
import com.forgottenspace.parsers.ai.AiComponentExit;
import com.forgottenspace.parsers.ai.AiComponentProperty;
import com.forgottenspace.parsers.entitytemplate.EntityTemplate;
import com.jme3.asset.AssetManager;

public class SpawnShipComponent extends AiComponent {

    @AiComponentProperty(name = SHIP_TEMPLATE_PROPERTY, displayName = "Ship Template", type = String.class, shortDescription = "Fully Qualified name for the ship template file.")
    private String shipTemplate;
    @AiComponentExit(name = SPAWNED_EXIT, displayName = "Spawned", type = String.class, shortDescription = "The ship has been spawned")
    private String spawned;

    public SpawnShipComponent(String id) {
        super(id);
    }

    @Override
    public String[] getMandatoryProperties() {
        return new String[]{SHIP_TEMPLATE_PROPERTY};
    }

    @Override
    public String[] getMandatoryExits() {
        return new String[]{SPAWNED_EXIT};
    }

    @Override
    public void initialiseProperties() {
        shipTemplate = (String) getProp(SHIP_TEMPLATE_PROPERTY);
        spawned = (String) exits.get(SPAWNED_EXIT);
        AssetManager assetManager = (AssetManager) getProp(ASSET_MANAGER_PROPERTY);
        if (assetManager != null) {
            EntityTemplate template = (EntityTemplate) assetManager.loadAsset(shipTemplate);
            Entity shipEntity = Entities.getInstance().createEntity(template.getComponentsAsArray());
            LocationComponent lc = Entities.getInstance().loadComponentForEntity(entity, LocationComponent.class);
            Entities.getInstance().addComponentsToEntity(shipEntity, lc);
            aiScript.setGlobalProp(SHIP_ENTITY_PROPERTY, shipEntity.getId());
            aiScript.setCurrentComponent(spawned);
        }
    }

    @Override
    public void updateProperties() {
        props.put(SHIP_TEMPLATE_PROPERTY, shipTemplate);
    }
}

package com.forgottenspace.loaders;

import com.forgottenspace.es.Entities;
import com.forgottenspace.es.Entity;
import com.forgottenspace.es.EntityComponent;
import com.forgottenspace.es.components.LocationComponent;
import com.forgottenspace.parsers.ParserException;
import com.forgottenspace.parsers.entitytemplate.EntityTemplate;
import com.jme3.asset.AssetManager;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;

class EntitySceneConverter implements SceneGraphVisitor {

    public static final String TEMPLATE_FILE_NAME = "templateFileName";
    
    private final AssetManager assetManager;
    private final Entities entities = Entities.getInstance();

    public EntitySceneConverter(AssetManager assetManager) {
		this.assetManager = assetManager;
	}

	public void visit(Spatial spatial) {
        if (spatial.getUserDataKeys().contains(TEMPLATE_FILE_NAME)) {
            convertSpatialToEntity(spatial);
        }
    }

    private void convertSpatialToEntity(Spatial spatial) {
        String templateFileName = spatial.getUserData(TEMPLATE_FILE_NAME);
        EntityTemplate template = (EntityTemplate) assetManager.loadAsset(templateFileName);
        if (template.getComponents() != null && !template.getComponents().isEmpty()) {
            Entity entity = convertTemplateToEntity(template);
            entities.addComponentsToEntity(entity, new LocationComponent(spatial.getWorldTranslation(), spatial.getWorldRotation(), spatial.getLocalScale()));
            spatial.removeFromParent();
        } else {
            throw new ParserException("No components for template " + templateFileName);
        }
    }

    private Entity convertTemplateToEntity(EntityTemplate template) {
        EntityComponent[] components = template.getComponentsAsArray();
        return entities.createEntity(components);
    }
}

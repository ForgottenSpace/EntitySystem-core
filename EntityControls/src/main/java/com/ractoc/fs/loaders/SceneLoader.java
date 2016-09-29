package com.ractoc.fs.loaders;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.ractoc.fs.components.es.LocationComponent;
import com.ractoc.fs.es.Entities;
import com.ractoc.fs.es.Entity;
import com.ractoc.fs.es.EntityComponent;
import com.ractoc.fs.parsers.ParserException;
import com.ractoc.fs.parsers.entitytemplate.EntityTemplate;
import com.ractoc.fs.parsers.entitytemplate.TemplateLoader;

public class SceneLoader {

    private final AssetManager assetManager;
    private final Entities entities = Entities.getInstance();
    private Node scene;

    public SceneLoader(AssetManager assetManager) {
        this.assetManager = assetManager;
        TemplateLoader.setClassLoader(this.getClass().getClassLoader());
        assetManager.registerLoader(TemplateLoader.class, "etpl", "ETPL");
    }

    public Node loadScene(String sceneFileName) {
        scene = (Node) assetManager.loadModel(sceneFileName);
        parseScene();
        return scene;
    }

    private void parseScene() {
        scene.depthFirstTraversal(new EntitySceneConverter());
    }

    private class EntitySceneConverter implements SceneGraphVisitor {

        public static final String TEMPLATE_FILE_NAME = "templateFileName";

        @Override
        public void visit(Spatial spatial) {
            if (spatial.getUserDataKeys().contains(TEMPLATE_FILE_NAME)) {
                convertSpatialToEntity(spatial);
            }
        }

        private void convertSpatialToEntity(Spatial spatial) {
            String templateFileName = spatial.getUserData(TEMPLATE_FILE_NAME);
            EntityTemplate template = (EntityTemplate) assetManager.loadAsset(templateFileName);
            if (template.getComponents() != null && template.getComponents().size() > 0) {
                Entity entity = convertTemplateToEntity(template);
                entities.addComponentsToEntity(entity, new LocationComponent(spatial.getWorldTranslation(), spatial.getWorldRotation(), spatial.getLocalScale()));
                spatial.removeFromParent();
            } else {
                throw new ParserException("No components for template " + templateFileName);
            }
        }

        private Entity convertTemplateToEntity(EntityTemplate template) {
            EntityComponent[] components = (EntityComponent[]) template.getComponentsAsArray();
            Entity entity = entities.createEntity(components);
            return entity;
        }
    }
}

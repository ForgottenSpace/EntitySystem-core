package com.ractoc.fs.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.collision.CollisionResults;
import com.jme3.input.ChaseCamera;
import com.jme3.math.Vector3f;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl;
import com.ractoc.fs.components.es.CanMoveComponent;
import com.ractoc.fs.components.es.ControlledComponent;
import com.ractoc.fs.components.es.FollowComponent;
import com.ractoc.fs.components.es.LocationComponent;
import com.ractoc.fs.components.es.RenderComponent;
import com.ractoc.fs.es.Entities;
import com.ractoc.fs.es.Entity;
import com.ractoc.fs.es.EntityException;
import com.ractoc.fs.es.EntityResultSet;
import com.ractoc.fs.es.EntityResultSet.UpdateProcessor;
import com.ractoc.fs.loaders.SceneLoader;
import java.util.List;

public class SceneAppState extends AbstractEntityControl {

    public static final String NODE_SCENE = "scene";
    public static final String NODE_ENTITY = "render_entity_";
    private String sceneFile;
    private EntityResultSet renderableResultSet;
    private EntityResultSet controlledResultSet;
    private EntityResultSet followedResultSet;
    private UpdateProcessor updateProcessor;
    private Entity controlledEntity;
    private Entity followedEntity;
    private Node sceneNode;
    private Node rootNode;

    public SceneAppState(String sceneFile) {
        this.sceneFile = sceneFile;
        queryResultSets();
    }

    public SceneAppState() {
        queryResultSets();
    }

    private void queryResultSets() {
        renderableResultSet = queryEntityResultSet(RenderComponent.class, LocationComponent.class);
        controlledResultSet = queryEntityResultSet(RenderComponent.class, LocationComponent.class, CanMoveComponent.class, ControlledComponent.class);
        followedResultSet = queryEntityResultSet(RenderComponent.class, LocationComponent.class, CanMoveComponent.class, FollowComponent.class);
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        rootNode = getApplication().getRootNode();
        if (sceneFile != null) {
            loadScene();
        } else {
            createEmptyScene();
        }
        rootNode.attachChild(sceneNode);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        determineControlledEntity();
        determineRenderableEntities();
        moveEntities();
        determineFollowEntity();
    }

    private void determineControlledEntity() throws EntityException {
        updateProcessor = controlledResultSet.getUpdateProcessor();
        determineRemovedControlledEntities();
        determineAddedControlledEntities();
        updateProcessor.finalizeUpdates();
    }

    private void determineRenderableEntities() {
        updateProcessor = renderableResultSet.getUpdateProcessor();
        determineAddedRenderableEntities();
        determineRemovedRenderableEntities();
        updateProcessor.finalizeUpdates();
    }

    private void loadScene() {
        SceneLoader sceneLoader = new SceneLoader(getAssetManager());
        sceneNode = sceneLoader.loadScene(sceneFile);
        sceneNode.setName(NODE_SCENE);
    }

    private void createEmptyScene() {
        sceneNode = new Node(NODE_SCENE);
    }

    private Spatial createEntitySpatialForEntity(Entity entity) {
        RenderComponent rc = Entities.getInstance().loadComponentForEntity(entity, RenderComponent.class);
        LocationComponent lc = Entities.getInstance().loadComponentForEntity(entity, LocationComponent.class);
        Spatial modelSpatial = getAssetManager().loadModel(rc.getJ3o());
        modelSpatial.setUserData("entity", entity.getId());
        modelSpatial.setName(NODE_ENTITY + entity.getId());
        modelSpatial.setLocalTranslation(lc.getTranslation());
        modelSpatial.setLocalRotation(lc.getRotation());
        modelSpatial.setLocalScale(lc.getScale());
        return modelSpatial;
    }

    private void determineAddedRenderableEntities() {
        List<Entity> entities = updateProcessor.getAddedEntities();
        for (Entity entity : entities) {
            Spatial modelSpatial = createEntitySpatialForEntity(entity);
            sceneNode.attachChild(modelSpatial);
        }
    }

    private void determineRemovedRenderableEntities() {
        List<Entity> entities = updateProcessor.getRemovedEntities();
        for (Entity entity : entities) {
            sceneNode.detachChildNamed(NODE_ENTITY + entity.getId());
        }
    }

    private void moveEntities() {
        for (Entity entity : renderableResultSet) {
            LocationComponent entityWorldLocation = Entities.getInstance().loadComponentForEntity(entity, LocationComponent.class);
            Spatial modelSpatial = sceneNode.getChild(NODE_ENTITY + entity.getId());
            Vector3f entityNodeLocation = entityWorldLocation.getTranslation();
            modelSpatial.setLocalTranslation(entityNodeLocation);
            modelSpatial.setLocalRotation(entityWorldLocation.getRotation());
        }
    }

    private void determineRemovedControlledEntities() throws EntityException {
        if (updateProcessor.getRemovedEntities().size() > 1) {
            throw new EntityException("Too many entities in resultset.");
        } else if (updateProcessor.getRemovedEntities().size() == 1) {
            determineRemovedControlledEntity();
        }
    }

    private void determineAddedControlledEntities() throws EntityException {
        if (updateProcessor.getAddedEntities().size() > 1) {
            throw new EntityException("Too many entities in resultset.");
        } else if (updateProcessor.getAddedEntities().size() == 1) {
            determineAddedControlledEntity();
        }
    }

    private void determineRemovedControlledEntity() throws EntityException {
        if (controlledEntity != null && controlledEntity.equals(updateProcessor.getRemovedEntities().get(0))) {
            controlledEntity = null;
            setEnabled(false);
        } else if (controlledEntity != null) {
            throw new EntityException("Trying to remove an invalid entity.");
        }
    }

    private void determineAddedControlledEntity() throws EntityException {
        if (controlledEntity != null) {
            throw new EntityException("Already controlling an entity.");
        } else {
            controlledEntity = updateProcessor.getAddedEntities().get(0);
            setEnabled(true);
        }
    }

    public Entity getCollidingEntity(Entity damageEntity) {
        Spatial damageSpatial = sceneNode.getChild(NODE_ENTITY + damageEntity.getId());
        CollisionResults collisionResults = new CollisionResults();
        if (damageSpatial != null) {
            for (Spatial entitySpatial : sceneNode.getChildren()) {
                if (entitySpatial != damageSpatial && damageSpatial.collideWith(entitySpatial.getWorldBound(), collisionResults) > 0) {
                    return Entities.getInstance().getEntityById((Long) entitySpatial.getUserData("entity"));
                }
            }
        }
        return null;
    }

    private void determineFollowEntity() {
        updateProcessor = followedResultSet.getUpdateProcessor();
        if (!updateProcessor.getRemovedEntities().isEmpty()) {
            setupCamera();
            followedEntity = null;
        }
        if (!updateProcessor.getAddedEntities().isEmpty()) {
            followedEntity = updateProcessor.getAddedEntities().get(0);
            setupFollowCamera();
        }
        updateProcessor.finalizeUpdates();
    }

    private void setupCamera() {
        Node followedSpatial = (Node) sceneNode.getChild(NODE_ENTITY + followedEntity.getId());
        followedSpatial.detachChildNamed("Camera Node");
        getApplication().getCamera().setLocation(new Vector3f(0, 60, 0));
        getApplication().getCamera().lookAt(Vector3f.ZERO, Vector3f.UNIT_Z);
    }

    private void setupFollowCamera() {
        Node followedSpatial = (Node) sceneNode.getChild(NODE_ENTITY + followedEntity.getId());
        FollowComponent followC = Entities.getInstance().loadComponentForEntity(followedEntity, FollowComponent.class);
        CameraNode camNode = new CameraNode("Camera Node", getApplication().getCamera());
        camNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
        followedSpatial.attachChild(camNode);
        System.out.println("FollowLocation: " + followC.getLocation());
        camNode.setLocalTranslation(followC.getLocation().clone());
        camNode.lookAt(followedSpatial.getLocalTranslation(), Vector3f.UNIT_Y);
//        ChaseCamera chaseCam = new ChaseCamera(getApplication().getCamera(), followedSpatial, getApplication().getInputManager());
//        chaseCam.setSmoothMotion(true);
    }
}

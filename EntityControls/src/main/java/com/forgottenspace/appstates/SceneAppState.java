package com.forgottenspace.appstates;

import java.util.List;

import com.forgottenspace.es.ComponentTypeCriteria;
import com.forgottenspace.es.Entities;
import com.forgottenspace.es.Entity;
import com.forgottenspace.es.EntityException;
import com.forgottenspace.es.EntityResultSet;
import com.forgottenspace.es.EntityResultSet.UpdateProcessor;
import com.forgottenspace.es.components.CanMoveComponent;
import com.forgottenspace.es.components.ControlledComponent;
import com.forgottenspace.es.components.LocationComponent;
import com.forgottenspace.es.components.RenderComponent;
import com.forgottenspace.loaders.SceneLoader;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class SceneAppState extends AbstractAppState {

	public static final String NODE_SCENE = "scene";
	public static final String NODE_ENTITY = "render_entity_";
	private String sceneFile;
	private EntityResultSet renderableResultSet;
	private EntityResultSet controlledResultSet;
	private UpdateProcessor updateProcessor;
	private Entity controlledEntity;
	private SimpleApplication application;
	private Node sceneNode;
	private Node rootNode;
	private boolean playerCentric = true;

	public SceneAppState(String sceneFile) {
		this.sceneFile = sceneFile;
		queryResultSets();
	}

	public SceneAppState() {
		queryResultSets();
	}

	private void queryResultSets() {
		queryRenderableResultSet();
		queryControlledResultSet();
	}

	private void queryRenderableResultSet() {
		Entities entities = Entities.getInstance();
		ComponentTypeCriteria criteria = new ComponentTypeCriteria(RenderComponent.class, LocationComponent.class);
		renderableResultSet = entities.queryEntities(criteria);
	}

	private void queryControlledResultSet() {
		Entities entities = Entities.getInstance();
		ComponentTypeCriteria controlledCriteria = new ComponentTypeCriteria(RenderComponent.class,
				LocationComponent.class, CanMoveComponent.class, ControlledComponent.class);
		controlledResultSet = entities.queryEntities(controlledCriteria);
	}

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		application = (SimpleApplication) app;
		rootNode = application.getRootNode();
		if (sceneFile != null) {
			loadScene();
		} else {
			createEmptyScene();
		}
		rootNode.attachChild(sceneNode);
	}

	@Override
	public void stateAttached(AppStateManager stateManager) {
		super.stateAttached(stateManager);
		if (rootNode != null) {
			rootNode.attachChild(sceneNode);
		}
	}

	@Override
	public void stateDetached(AppStateManager stateManager) {
		super.stateDetached(stateManager);
		rootNode.detachChild(sceneNode);
	}

	@Override
	public void update(float tpf) {
		super.update(tpf);
		determineControlledEntity();
		if (controlledEntity == null) {
			application.stop();
		} else {
			determineRenderableEntities();
			moveEntities();
		}
	}

	private void determineControlledEntity() {
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
		SceneLoader sceneLoader = new SceneLoader(application.getAssetManager());
		sceneNode = sceneLoader.loadScene(sceneFile);
		sceneNode.setName(NODE_SCENE);
	}

	private void createEmptyScene() {
		sceneNode = new Node(NODE_SCENE);
	}

	private Spatial createEntitySpatialForEntity(Entity entity) {
		RenderComponent rc = Entities.getInstance().loadComponentForEntity(entity, RenderComponent.class);
		LocationComponent lc = Entities.getInstance().loadComponentForEntity(entity, LocationComponent.class);
		Spatial modelSpatial = application.getAssetManager().loadModel(rc.getJ3o());
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
		LocationComponent controlledWorldLocation = Entities.getInstance().loadComponentForEntity(controlledEntity,
				LocationComponent.class);

		for (Entity entity : renderableResultSet) {
			LocationComponent entityWorldLocation = Entities.getInstance().loadComponentForEntity(entity,
					LocationComponent.class);
			Spatial modelSpatial = sceneNode.getChild(NODE_ENTITY + entity.getId());
			Vector3f entityNodeLocation = entityWorldLocation.getTranslation();
			if (isPlayerCentric()) {
				entityNodeLocation = entityNodeLocation.subtract(controlledWorldLocation.getTranslation());
			}

			modelSpatial.setLocalTranslation(entityNodeLocation);
			modelSpatial.setLocalRotation(entityWorldLocation.getRotation());
		}
	}

	private void determineRemovedControlledEntities() {
		if (updateProcessor.getRemovedEntities().size() > 1) {
			throw new EntityException("Too many entities in resultset.");
		} else if (updateProcessor.getRemovedEntities().size() == 1) {
			determineRemovedControlledEntity();
		}
	}

	private void determineAddedControlledEntities() {
		if (updateProcessor.getAddedEntities().size() > 1) {
			throw new EntityException("Too many entities in resultset.");
		} else if (updateProcessor.getAddedEntities().size() == 1) {
			determineAddedControlledEntity();
		}
	}

	private void determineRemovedControlledEntity() {
		if (controlledEntity != null && controlledEntity.equals(updateProcessor.getRemovedEntities().get(0))) {
			controlledEntity = null;
			setEnabled(false);
		} else if (controlledEntity != null) {
			throw new EntityException("Trying to remove an invalid entity.");
		}
	}

	private void determineAddedControlledEntity() {
		if (controlledEntity != null) {
			throw new EntityException("Already controlling an entity.");
		} else {
			controlledEntity = updateProcessor.getAddedEntities().get(0);
			setEnabled(true);
		}
	}

	public boolean isPlayerCentric() {
		return playerCentric;
	}

	public void setPlayerCentric(boolean playerCentric) {
		this.playerCentric = playerCentric;
	}

	public Entity getCollidingEntity(Entity damageEntity) {
		Spatial damageSpatial = sceneNode.getChild(NODE_ENTITY + damageEntity.getId());
		CollisionResults collisionResults = new CollisionResults();
		if (damageSpatial != null) {
			for (Spatial entitySpatial : sceneNode.getChildren()) {
				if (entitySpatial != damageSpatial
						&& damageSpatial.collideWith(entitySpatial.getWorldBound(), collisionResults) > 0) {
					return Entities.getInstance().getEntityById((Long) entitySpatial.getUserData("entity"));
				}
			}
		}
		return null;
	}
}
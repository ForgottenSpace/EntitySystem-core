package com.forgottenspace.appstates;

import java.util.ArrayList;
import java.util.List;

import com.forgottenspace.es.ComponentTypeCriteria;
import com.forgottenspace.es.Entities;
import com.forgottenspace.es.Entity;
import com.forgottenspace.es.EntityResultSet;
import com.forgottenspace.es.components.DamageComponent;
import com.forgottenspace.es.components.LocationComponent;
import com.forgottenspace.es.components.OriginComponent;
import com.forgottenspace.es.components.StructureComponent;
import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;

public class DamageAppState extends AbstractEntityControl {

    private EntityResultSet resultSet;
    private List<Entity> damageEntities = new ArrayList<>();
    private AppStateManager appStateManager;

    @SuppressWarnings("unchecked")
	public DamageAppState() {
        resultSet = queryEntityResultSet(DamageComponent.class, LocationComponent.class);
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        appStateManager = stateManager;
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        EntityResultSet.UpdateProcessor updateProcessor = resultSet.getUpdateProcessor();
        damageEntities.addAll(updateProcessor.getAddedEntities());
        damageEntities.removeAll(updateProcessor.getRemovedEntities());
        updateProcessor.finalizeUpdates();

        processDamageForEntities();
    }

	private void processDamageForEntities() {
        SceneAppState scene = appStateManager.getState(SceneAppState.class);

        for (Entity damageEntity : damageEntities) {
            processDamageForEntity(scene, damageEntity);
        }
    }

	private void processDamageForEntity(SceneAppState scene, Entity damageEntity) {
		Entity collidingEntity = scene.getCollidingEntity(damageEntity);
		if (collidingEntity != null) {
		    processPossibleHit(damageEntity, collidingEntity);
		}
	}

	private void processPossibleHit(Entity damageEntity, Entity collidingEntity) {
		Long originEntityId = Entities.getInstance().loadComponentForEntity(damageEntity, OriginComponent.class).getOriginEntityId();
		if (!originEntityId.equals(collidingEntity.getId()) && collidingEntity.matches(new ComponentTypeCriteria(StructureComponent.class))) {
		    processHit(damageEntity, collidingEntity);
		}
	}

	private void processHit(Entity damageEntity, Entity collidingEntity) {
		int hp = calculateNewHitpoints(damageEntity, collidingEntity);
		newHitpointsEffect(collidingEntity, hp);
		Entities.getInstance().destroyEntity(damageEntity);
	}

	private void newHitpointsEffect(Entity collidingEntity, int hp) {
		if (hp <= 0) {
		    Entities.getInstance().destroyEntity(collidingEntity);
		} else {
		    Entities.getInstance().changeComponentsForEntity(collidingEntity, new StructureComponent(hp));
		}
	}

	private int calculateNewHitpoints(Entity damageEntity, Entity collidingEntity) {
		StructureComponent struct = Entities.getInstance().loadComponentForEntity(collidingEntity, StructureComponent.class);
		int hp = struct.getHitpoints();
		DamageComponent damage = Entities.getInstance().loadComponentForEntity(damageEntity, DamageComponent.class);
		hp -= damage.getDamage();
		return hp;
	}
}

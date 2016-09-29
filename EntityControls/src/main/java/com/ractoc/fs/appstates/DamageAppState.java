package com.ractoc.fs.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.ractoc.fs.components.es.DamageComponent;
import com.ractoc.fs.components.es.LocationComponent;
import com.ractoc.fs.components.es.OriginComponent;
import com.ractoc.fs.components.es.StructureComponent;
import com.ractoc.fs.es.ComponentTypeCriteria;
import com.ractoc.fs.es.Entities;
import com.ractoc.fs.es.Entity;
import com.ractoc.fs.es.EntityResultSet;
import java.util.ArrayList;
import java.util.List;

public class DamageAppState extends AbstractEntityControl {

    private EntityResultSet resultSet;
    private List<Entity> damageEntities = new ArrayList<Entity>();
    private AppStateManager appStateManager;

    public DamageAppState() {
        resultSet = queryEntityResultSet(DamageComponent.class, LocationComponent.class);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        EntityResultSet.UpdateProcessor updateProcessor = resultSet.getUpdateProcessor();
        damageEntities.addAll(updateProcessor.getAddedEntities());
        damageEntities.removeAll(updateProcessor.getRemovedEntities());
        updateProcessor.finalizeUpdates();

        processDamage(tpf);
    }

    private void processDamage(float tpf) {
        SceneAppState scene = appStateManager.getState(SceneAppState.class);

        for (Entity damageEntity : damageEntities) {
            Entity collidingEntity = scene.getCollidingEntity(damageEntity);
            if (collidingEntity != null) {
                Long originEntityId = Entities.getInstance().loadComponentForEntity(damageEntity, OriginComponent.class).getOriginEntityId();
                if (!originEntityId.equals(collidingEntity.getId()) && collidingEntity.matches(new ComponentTypeCriteria(StructureComponent.class))) {
                    StructureComponent struct = Entities.getInstance().loadComponentForEntity(collidingEntity, StructureComponent.class);
                    int hp = struct.getHitpoints();
                    DamageComponent damage = Entities.getInstance().loadComponentForEntity(damageEntity, DamageComponent.class);
                    hp -= damage.getDamage();
                    if (hp <= 0) {
                        System.out.println("BOOM!!!");
                        Entities.getInstance().destroyEntity(collidingEntity);
                    } else {
                        Entities.getInstance().changeComponentsForEntity(collidingEntity, new StructureComponent(hp));
                    }
                    Entities.getInstance().destroyEntity(damageEntity);
                }
            }
        }
    }
}

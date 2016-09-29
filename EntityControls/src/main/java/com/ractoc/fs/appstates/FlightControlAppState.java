package com.ractoc.fs.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.ractoc.fs.components.es.*;
import com.ractoc.fs.es.ComponentTypeCriteria;
import com.ractoc.fs.es.Entities;
import com.ractoc.fs.es.Entity;
import com.ractoc.fs.es.EntityException;
import com.ractoc.fs.es.EntityResultSet;
import java.util.List;

public class FlightControlAppState extends AbstractEntityControl implements ActionListener {

    private EntityResultSet resultSet;
    private Entity controlledEntity;
    private MovementComponent movementComponent;

    public FlightControlAppState() {
        resultSet = queryEntityResultSet(RenderComponent.class, LocationComponent.class, CanMoveComponent.class, ControlledComponent.class);
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        setupControlsWithInputManager(app.getInputManager());
    }

    private void setupControlsWithInputManager(InputManager inputManager) {
        inputManager.addListener(this,
                                 Controls.MOVE_FORWARD.name(),
                                 Controls.MOVE_BACKWARDS.name(),
                                 Controls.STRAFE_RIGHT.name(),
                                 Controls.STRAFE_LEFT.name(),
                                 Controls.ROTATE_LEFT.name(),
                                 Controls.ROTATE_RIGHT.name(),
                                 Controls.SHOOT_MAIN.name());
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        EntityResultSet.UpdateProcessor updateProcessor = resultSet.getUpdateProcessor();
        updateRemovedEntities(updateProcessor.getRemovedEntities());
        updateAddedEntities(updateProcessor.getAddedEntities());
        updateProcessor.finalizeUpdates();
    }

    private void updateRemovedEntities(List<Entity> entities) throws EntityException {
        if (entities.size() > 1) {
            throw new EntityException("Too many entities in resultset.");
        } else if (entities.size() == 1) {
            updateRemovedEntity(entities);
        }
    }

    private void updateRemovedEntity(List<Entity> entities) throws EntityException {
        System.out.println("removing controlled entity");
        if (controlledEntity == null) {
            throw new EntityException("No entity to remove.");
        } else if (controlledEntity.equals(entities.get(0))) {
            controlledEntity = null;
            setEnabled(false);
        }
    }

    private void updateAddedEntities(List<Entity> entities) throws EntityException {
        if (entities.size() > 1) {
            throw new EntityException("Too many entities in resultset.");
        } else if (entities.size() == 1) {
            updateAddedEntity(entities);
        }
    }

    private void updateAddedEntity(List<Entity> entities) throws EntityException {
        if (controlledEntity != null) {
            throw new EntityException("Already controlling an entity.");
        } else {
            controlledEntity = entities.get(0);
            setEnabled(true);
        }
    }

    @Override
    public final void onAction(final String name,
                               final boolean isPressed,
                               final float tpf) {
        if (isEnabled()) {
            loadMovementComponent();
            if (isEnabled() && !isPressed) {
                stop(name);
            } else if (isEnabled() && isPressed) {
                start(name);
            }
        }
    }

    private boolean isAnyMovementButton(final String name) {
        return name.equals(Controls.MOVE_FORWARD.getName())
                || name.equals(Controls.MOVE_BACKWARDS.getName());
    }

    private void stopMoving() {
        Entities.getInstance().changeComponentsForEntity(controlledEntity, new MovementComponent(
                false,
                false,
                movementComponent.isStrafeLeft(),
                movementComponent.isStrafeRight(),
                movementComponent.isRotateLeft(),
                movementComponent.isRotateRight()));
    }

    private boolean isAnyStrafeButton(final String name) {
        return name.equals(Controls.STRAFE_LEFT.getName())
                || name.equals(Controls.STRAFE_RIGHT.getName());
    }

    private void stopStrafing() {
        Entities.getInstance().changeComponentsForEntity(controlledEntity, new MovementComponent(
                movementComponent.isMoveForward(),
                movementComponent.isMoveBackwards(),
                false,
                false,
                movementComponent.isRotateLeft(),
                movementComponent.isRotateRight()));
    }

    private boolean isAnyRotateButton(final String name) {
        return name.equals(Controls.ROTATE_LEFT.getName())
                || name.equals(Controls.ROTATE_RIGHT.getName());
    }

    private void stopRotating() {
        Entities.getInstance().changeComponentsForEntity(controlledEntity, new MovementComponent(
                movementComponent.isMoveForward(),
                movementComponent.isMoveBackwards(),
                movementComponent.isStrafeLeft(),
                movementComponent.isStrafeRight(),
                false,
                false));
    }

    private void start(String name) {
        if (name.equals(Controls.MOVE_FORWARD.getName())) {
            moveForward();
        } else if (name.equals(Controls.MOVE_BACKWARDS.getName())) {
            moveBackwards();
        } else if (name.equals(Controls.STRAFE_LEFT.getName())) {
            strafeLeft();
        } else if (name.equals(Controls.STRAFE_RIGHT.getName())) {
            strafeRight();
        } else if (name.equals(Controls.ROTATE_LEFT.getName())) {
            rotateLeft();
        } else if (name.equals(Controls.ROTATE_RIGHT.getName())) {
            rotateRight();
        } else if (name.equals(Controls.SHOOT_MAIN.getName())) {
            shootMain();
        }
    }

    private void moveForward() {
        Entities.getInstance().changeComponentsForEntity(controlledEntity, new MovementComponent(
                true,
                false,
                movementComponent.isStrafeLeft(),
                movementComponent.isStrafeRight(),
                movementComponent.isRotateLeft(),
                movementComponent.isRotateRight()));
    }

    private void moveBackwards() {
        Entities.getInstance().changeComponentsForEntity(controlledEntity, new MovementComponent(
                false,
                true,
                movementComponent.isStrafeLeft(),
                movementComponent.isStrafeRight(),
                movementComponent.isRotateLeft(),
                movementComponent.isRotateRight()));
    }

    private void strafeLeft() {
        Entities.getInstance().changeComponentsForEntity(controlledEntity, new MovementComponent(
                movementComponent.isMoveForward(),
                movementComponent.isMoveBackwards(),
                true,
                false,
                movementComponent.isRotateLeft(),
                movementComponent.isRotateRight()));
    }

    private void strafeRight() {
        Entities.getInstance().changeComponentsForEntity(controlledEntity, new MovementComponent(
                movementComponent.isMoveForward(),
                movementComponent.isMoveBackwards(),
                false,
                true,
                movementComponent.isRotateLeft(),
                movementComponent.isRotateRight()));
    }

    private void rotateLeft() {
        Entities.getInstance().changeComponentsForEntity(controlledEntity, new MovementComponent(
                movementComponent.isMoveForward(),
                movementComponent.isMoveBackwards(),
                movementComponent.isStrafeLeft(),
                movementComponent.isStrafeRight(),
                true,
                false));
    }

    private void rotateRight() {
        Entities.getInstance().changeComponentsForEntity(controlledEntity, new MovementComponent(
                movementComponent.isMoveForward(),
                movementComponent.isMoveBackwards(),
                movementComponent.isStrafeLeft(),
                movementComponent.isStrafeRight(),
                false,
                true));
    }

    private boolean entityHasMovementComponent() {
        return controlledEntity.matches(new ComponentTypeCriteria(MovementComponent.class));
    }

    private void loadMovementComponent() {
        if (entityHasMovementComponent()) {
            movementComponent = Entities.getInstance().loadComponentForEntity(controlledEntity, MovementComponent.class);
        } else {
            setDefaultComponents();
        }
    }

    private void setDefaultComponents() {
        movementComponent = new MovementComponent(false, false, false, false, false, false);
        Entities.getInstance().addComponentsToEntity(controlledEntity, movementComponent);
        Entities.getInstance().addComponentsToEntity(controlledEntity, new SpeedComponent(0F, 0F, 0F));
    }

    private void stop(final String name) {
        if (isAnyMovementButton(name)) {
            stopMoving();
        } else if (isAnyStrafeButton(name)) {
            stopStrafing();
        } else if (isAnyRotateButton(name)) {
            stopRotating();
        } else if (name.equals(Controls.SHOOT_MAIN.getName())) {
            stopShooting();
        }
    }

    private void shootMain() {
        Entities.getInstance().addComponentsToEntity(controlledEntity, new ShootMainComponent(0f));
    }

    private void stopShooting() {
        Entities.getInstance().removeComponentsFromEntity(controlledEntity, new ShootMainComponent(0f));
    }
}

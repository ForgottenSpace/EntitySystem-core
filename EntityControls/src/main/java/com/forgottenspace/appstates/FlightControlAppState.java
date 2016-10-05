package com.forgottenspace.appstates;

import java.util.List;

import com.forgottenspace.es.ComponentTypeCriteria;
import com.forgottenspace.es.Entities;
import com.forgottenspace.es.Entity;
import com.forgottenspace.es.EntityException;
import com.forgottenspace.es.EntityResultSet;
import com.forgottenspace.es.components.CanMoveComponent;
import com.forgottenspace.es.components.ControlledComponent;
import com.forgottenspace.es.components.Controls;
import com.forgottenspace.es.components.ControlsException;
import com.forgottenspace.es.components.LocationComponent;
import com.forgottenspace.es.components.MovementComponent;
import com.forgottenspace.es.components.RenderComponent;
import com.forgottenspace.es.components.ShootMainComponent;
import com.forgottenspace.es.components.SpeedComponent;
import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;

public class FlightControlAppState extends AbstractEntityControl implements ActionListener {

	private EntityResultSet resultSet;
	private Entity controlledEntity;
	private MovementComponent movementComponent;

	@SuppressWarnings("unchecked")
	public FlightControlAppState() {
		resultSet = queryEntityResultSet(RenderComponent.class, LocationComponent.class, CanMoveComponent.class,
				ControlledComponent.class);
	}

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		setupControlsWithInputManager(app.getInputManager());
	}

	private void setupControlsWithInputManager(InputManager inputManager) {
		inputManager.addListener(this, Controls.MOVE_FORWARD.name(), Controls.MOVE_BACKWARDS.name(),
				Controls.STRAFE_RIGHT.name(), Controls.STRAFE_LEFT.name(), Controls.ROTATE_LEFT.name(),
				Controls.ROTATE_RIGHT.name(), Controls.SHOOT_MAIN.name());
	}

	@Override
	public void update(float tpf) {
		super.update(tpf);
		EntityResultSet.UpdateProcessor updateProcessor = resultSet.getUpdateProcessor();
		updateRemovedEntities(updateProcessor.getRemovedEntities());
		updateAddedEntities(updateProcessor.getAddedEntities());
		updateProcessor.finalizeUpdates();
	}

	private void updateRemovedEntities(List<Entity> entities) {
		if (entities.size() > 1) {
			throw new EntityException("Too many entities in resultset.");
		} else if (entities.size() == 1) {
			updateRemovedEntity(entities);
		}
	}

	private void updateRemovedEntity(List<Entity> entities) {
		if (controlledEntity == null) {
			throw new EntityException("No entity to remove.");
		} else if (controlledEntity.equals(entities.get(0))) {
			controlledEntity = null;
			setEnabled(false);
		}
	}

	private void updateAddedEntities(List<Entity> entities) {
		if (entities.size() > 1) {
			throw new EntityException("Too many entities in resultset.");
		} else if (entities.size() == 1) {
			updateAddedEntity(entities);
		}
	}

	private void updateAddedEntity(List<Entity> entities) {
		if (controlledEntity != null) {
			throw new EntityException("Already controlling an entity.");
		} else {
			controlledEntity = entities.get(0);
			setEnabled(true);
		}
	}

	public final void onAction(final String name, final boolean isPressed, final float tpf) {
		if (isEnabled()) {
			loadMovementComponent();
			Controls button = Controls.getControlByName(name);
			if (isPressed) {
				start(button);
			} else {
				stop(button);
			}
		}
	}

	private void start(Controls button) {
		switch (button) {
		case MOVE_FORWARD:
			moveForward();
			break;
		case MOVE_BACKWARDS:
			moveBackwards();
			break;
		case STRAFE_LEFT:
			strafeLeft();
			break;
		case STRAFE_RIGHT:
			strafeRight();
			break;
		case ROTATE_LEFT:
			rotateLeft();
			break;
		case ROTATE_RIGHT:
			rotateRight();
			break;
		case SHOOT_MAIN:
			shootMain();
			break;
		default:
			throw new ControlsException("Button " + button + " not implemented");
		}
	}

	private void stop(final Controls button) {
		switch (button) {
		case MOVE_FORWARD:
		case MOVE_BACKWARDS:
			stopMoving();
			break;
		case STRAFE_LEFT:
		case STRAFE_RIGHT:
			stopStrafing();
			break;
		case ROTATE_LEFT:
		case ROTATE_RIGHT:
			stopRotating();
			break;
		case SHOOT_MAIN:
			stopShooting();
			break;
		default:
			throw new ControlsException("Button " + button + " not implemented");
		}
	}

	private void moveForward() {
		Entities.getInstance().changeComponentsForEntity(controlledEntity,
				new MovementComponent(true, false, movementComponent.isStrafeLeft(), movementComponent.isStrafeRight(),
						movementComponent.isRotateLeft(), movementComponent.isRotateRight()));
	}

	private void moveBackwards() {
		Entities.getInstance().changeComponentsForEntity(controlledEntity,
				new MovementComponent(false, true, movementComponent.isStrafeLeft(), movementComponent.isStrafeRight(),
						movementComponent.isRotateLeft(), movementComponent.isRotateRight()));
	}

	private void stopMoving() {
		Entities.getInstance().changeComponentsForEntity(controlledEntity,
				new MovementComponent(false, false, movementComponent.isStrafeLeft(), movementComponent.isStrafeRight(),
						movementComponent.isRotateLeft(), movementComponent.isRotateRight()));
	}

	private void strafeLeft() {
		Entities.getInstance().changeComponentsForEntity(controlledEntity,
				new MovementComponent(movementComponent.isMoveForward(), movementComponent.isMoveBackwards(), true,
						false, movementComponent.isRotateLeft(), movementComponent.isRotateRight()));
	}

	private void strafeRight() {
		Entities.getInstance().changeComponentsForEntity(controlledEntity,
				new MovementComponent(movementComponent.isMoveForward(), movementComponent.isMoveBackwards(), false,
						true, movementComponent.isRotateLeft(), movementComponent.isRotateRight()));
	}

	private void stopStrafing() {
		Entities.getInstance().changeComponentsForEntity(controlledEntity,
				new MovementComponent(movementComponent.isMoveForward(), movementComponent.isMoveBackwards(), false,
						false, movementComponent.isRotateLeft(), movementComponent.isRotateRight()));
	}

	private void rotateLeft() {
		Entities.getInstance().changeComponentsForEntity(controlledEntity,
				new MovementComponent(movementComponent.isMoveForward(), movementComponent.isMoveBackwards(),
						movementComponent.isStrafeLeft(), movementComponent.isStrafeRight(), true, false));
	}

	private void rotateRight() {
		Entities.getInstance().changeComponentsForEntity(controlledEntity,
				new MovementComponent(movementComponent.isMoveForward(), movementComponent.isMoveBackwards(),
						movementComponent.isStrafeLeft(), movementComponent.isStrafeRight(), false, true));
	}

	private void stopRotating() {
		Entities.getInstance().changeComponentsForEntity(controlledEntity,
				new MovementComponent(movementComponent.isMoveForward(), movementComponent.isMoveBackwards(),
						movementComponent.isStrafeLeft(), movementComponent.isStrafeRight(), false, false));
	}

	private void shootMain() {
		Entities.getInstance().addComponentsToEntity(controlledEntity, new ShootMainComponent(0f));
	}

	private void stopShooting() {
		Entities.getInstance().removeComponentsFromEntity(controlledEntity, new ShootMainComponent(0f));
	}

	private boolean entityHasMovementComponent() {
		return controlledEntity.matches(new ComponentTypeCriteria(MovementComponent.class));
	}

	private void loadMovementComponent() {
		if (entityHasMovementComponent()) {
			movementComponent = Entities.getInstance().loadComponentForEntity(controlledEntity,
					MovementComponent.class);
		} else {
			setDefaultComponents();
		}
	}

	private void setDefaultComponents() {
		movementComponent = new MovementComponent(false, false, false, false, false, false);
		Entities.getInstance().addComponentsToEntity(controlledEntity, movementComponent);
		Entities.getInstance().addComponentsToEntity(controlledEntity, new SpeedComponent(0F, 0F, 0F));
	}
}

package com.forgottenspace.appstates;

import com.forgottenspace.es.ComponentTypeCriteria;
import com.forgottenspace.es.Entities;
import com.forgottenspace.es.Entity;
import com.forgottenspace.es.EntityResultSet;
import com.forgottenspace.es.components.BoundedEntityComponent;
import com.forgottenspace.es.components.CanMoveComponent;
import com.forgottenspace.es.components.LocationComponent;
import com.forgottenspace.es.components.MovementComponent;
import com.forgottenspace.es.components.SpeedComponent;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

public class FlightAppState extends AbstractEntityControl {

	private SimpleApplication application;
	private EntityResultSet resultSet;
	private boolean bounded;

	public FlightAppState() {
		queryEntityResultSet();
	}

	private void queryEntityResultSet() {
		Entities entities = Entities.getInstance();
		ComponentTypeCriteria criteria = new ComponentTypeCriteria(CanMoveComponent.class, SpeedComponent.class,
				MovementComponent.class, LocationComponent.class);
		resultSet = entities.queryEntities(criteria);
	}

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		application = (SimpleApplication) app;
	}

	@Override
	public void stateAttached(AppStateManager stateManager) {
		super.stateAttached(stateManager);
		setEnabled(true);
	}

	@Override
	public void stateDetached(AppStateManager stateManager) {
		super.stateDetached(stateManager);
		setEnabled(false);
	}

	@Override
	public void update(float tpf) {
		super.update(tpf);
		EntityResultSet.UpdateProcessor updateProcessor = resultSet.getUpdateProcessor();
		updateProcessor.finalizeUpdates();
		for (Entity movingEntity : resultSet) {
			updateMovingEntity(movingEntity, tpf);
		}
		updateProcessor.finalizeUpdates();
	}

	private void updateMovingEntity(Entity movingEntity, float tpf) {
		MovementComponent mc = Entities.getInstance().loadComponentForEntity(movingEntity, MovementComponent.class);
		CanMoveComponent cmc = Entities.getInstance().loadComponentForEntity(movingEntity, CanMoveComponent.class);
		LocationComponent lc = Entities.getInstance().loadComponentForEntity(movingEntity, LocationComponent.class);
		SpeedComponent sc = Entities.getInstance().loadComponentForEntity(movingEntity, SpeedComponent.class);

		sc = setSpeeds(sc, mc, cmc);

		Vector3f location = lc.getTranslation();
		Quaternion rotation = lc.getRotation();

		if (sc.getMoveSpeed() == 0 && sc.getStrafeSpeed() == 0 && sc.getRotationSpeed() == 0) {
			Entities.getInstance().removeComponentsFromEntity(movingEntity, sc, mc);
		} else {
			location = moveEntity(sc.getMoveSpeed() * tpf, location, rotation);
			location = strafeEntity(sc.getStrafeSpeed() * tpf, location, rotation);
			rotateEntity(sc.getRotationSpeed() * tpf, rotation);
			if (!isBounded(movingEntity) || onScreen(location)) {
				Entities.getInstance().changeComponentsForEntity(movingEntity, sc,
						createLocationComponent(location, rotation, lc));
			} else {
				Entities.getInstance().changeComponentsForEntity(movingEntity, sc,
						createLocationComponent(lc.getTranslation(), rotation, lc));
			}
		}
	}

	private Vector3f moveEntity(float movementSpeed, Vector3f location, Quaternion rotation) {
		if (movementSpeed != 0) {
			location = location.add(rotation.mult(Vector3f.UNIT_Z).normalizeLocal().multLocal(movementSpeed));
		}
		return location;
	}

	private Vector3f strafeEntity(float strafeSpeed, Vector3f location, Quaternion rotation) {
		if (strafeSpeed != 0) {
			location = location.add(rotation.mult(Vector3f.UNIT_X).normalizeLocal().multLocal(strafeSpeed));
		}
		return location;
	}

	private void rotateEntity(float rotateSpeed, Quaternion rotation) {
		if (rotateSpeed != 0) {
			Quaternion rot = new Quaternion();
			rot.fromAngles(0f, rotateSpeed, 0f);
			rotation.multLocal(rot);
		}
	}

	private LocationComponent createLocationComponent(Vector3f location, Quaternion rotation, LocationComponent lc) {
		return new LocationComponent(location, rotation, lc.getScale());
	}

	private SpeedComponent setSpeeds(SpeedComponent sc, MovementComponent mc, CanMoveComponent cmc) {
		Float movementSpeed = setMovementSpeed(sc, mc, cmc);
		Float strafeSpeed = setStrafeSpeed(sc, mc, cmc);
		Float rotationSpeed = setRotationSpeed(mc, cmc.getTurnSpeed());
		return new SpeedComponent(movementSpeed, strafeSpeed, rotationSpeed);
	}

	private Float setMovementSpeed(SpeedComponent sc, MovementComponent mc, CanMoveComponent cmc) {
		float movementSpeed = sc.getMoveSpeed();
		if (mc.isMoveForward()) {
			movementSpeed = moveForward(movementSpeed, cmc);
		} else if (mc.isMoveBackwards()) {
			movementSpeed = moveBackwards(movementSpeed, cmc);
		} else if (movementSpeed > 0) {
			movementSpeed = decelerateMovement(movementSpeed, cmc);
		} else if (movementSpeed < 0) {
			movementSpeed = accelerateMovement(movementSpeed, cmc);
		}
		return movementSpeed;
	}

	private float moveForward(float movementSpeed, CanMoveComponent cmc) {
		movementSpeed += cmc.getAcceleration();
		if (movementSpeed > cmc.getMaxSpeed()) {
			movementSpeed = cmc.getMaxSpeed();
		}
		return movementSpeed;
	}

	private float moveBackwards(float movementSpeed, CanMoveComponent cmc) {
		movementSpeed -= cmc.getBrake();
		if (movementSpeed < -cmc.getMaxSpeed()) {
			movementSpeed = -cmc.getMaxSpeed();
		}
		return movementSpeed;
	}

	private float decelerateMovement(float movementSpeed, CanMoveComponent cmc) {
		movementSpeed -= cmc.getDeceleration();
		if (movementSpeed < 0) {
			movementSpeed = 0;
		}
		return movementSpeed;
	}

	private float accelerateMovement(float movementSpeed, CanMoveComponent cmc) {
		movementSpeed += cmc.getDeceleration();
		if (movementSpeed > 0) {
			movementSpeed = 0;
		}
		return movementSpeed;
	}

	private Float setStrafeSpeed(SpeedComponent sc, MovementComponent mc, CanMoveComponent cmc) {
		float strafeSpeed = sc.getStrafeSpeed();
		if (mc.isStrafeLeft()) {
			strafeSpeed = strafeLeft(strafeSpeed, cmc);
		} else if (mc.isStrafeRight()) {
			strafeSpeed = strafeRight(strafeSpeed, cmc);
		} else if (strafeSpeed > 0) {
			strafeSpeed = decelerateStrafing(strafeSpeed, cmc.getDeceleration());
		} else if (strafeSpeed < 0) {
			strafeSpeed = accelerateStrafing(strafeSpeed, cmc.getDeceleration());
		}
		return strafeSpeed;
	}

	private float strafeLeft(float strafeSpeed, CanMoveComponent cmc) {
		strafeSpeed += cmc.getAcceleration();
		if (strafeSpeed > cmc.getMaxSpeed()) {
			strafeSpeed = cmc.getMaxSpeed();
		}
		return strafeSpeed;
	}

	private float strafeRight(float strafeSpeed, CanMoveComponent cmc) {
		strafeSpeed -= cmc.getBrake();
		if (strafeSpeed < -cmc.getMaxSpeed()) {
			strafeSpeed = -cmc.getMaxSpeed();
		}
		return strafeSpeed;
	}

	private float decelerateStrafing(float strafeSpeed, float deceleration) {
		strafeSpeed -= deceleration;
		if (strafeSpeed < 0) {
			strafeSpeed = 0;
		}
		return strafeSpeed;
	}

	private float accelerateStrafing(float strafeSpeed, float deceleration) {
		strafeSpeed += deceleration;
		if (strafeSpeed > 0) {
			strafeSpeed = 0;
		}
		return strafeSpeed;
	}

	private Float setRotationSpeed(MovementComponent mc, float turnSpeed) {
		float rotationSpeed = 0F;
		if (mc.isRotateLeft()) {
			rotationSpeed = turnSpeed;
		} else if (mc.isRotateRight()) {
			rotationSpeed = -turnSpeed;
		}
		return rotationSpeed;
	}

	private boolean onScreen(Vector3f location) {
		boolean onScreen = true;
		Vector3f screenLocation = application.getCamera().getScreenCoordinates(location);
		if (screenLocation.x > application.getContext().getSettings().getWidth() || screenLocation.x < 0) {
			onScreen = false;
		}
		if (screenLocation.y > application.getContext().getSettings().getHeight() || screenLocation.y < 0) {
			onScreen = false;
		}
		return onScreen;
	}

	private boolean isBounded(Entity movingEntity) {
		return isBounded() && movingEntity.matches(new ComponentTypeCriteria(BoundedEntityComponent.class));
	}

	public boolean isBounded() {
		return bounded;
	}

	public void setBounded(boolean bounded) {
		this.bounded = bounded;
	}
}

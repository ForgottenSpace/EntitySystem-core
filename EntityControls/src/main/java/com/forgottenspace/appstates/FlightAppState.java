package com.forgottenspace.appstates;

import com.forgottenspace.es.Entities;
import com.forgottenspace.es.Entity;
import com.forgottenspace.es.EntityResultSet;
import com.forgottenspace.es.components.CanMoveComponent;
import com.forgottenspace.es.components.LocationComponent;
import com.forgottenspace.es.components.MovementComponent;
import com.forgottenspace.es.components.SpeedComponent;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

public class FlightAppState extends AbstractEntityControl {

	private EntityResultSet resultSet;
	private Entities entities = Entities.getInstance();

	@SuppressWarnings("unchecked")
	public FlightAppState() {
		resultSet = queryEntityResultSet(CanMoveComponent.class, SpeedComponent.class, MovementComponent.class,
				LocationComponent.class);
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
		MovementComponent mc = entities.loadComponentForEntity(movingEntity, MovementComponent.class);
		CanMoveComponent cmc = entities.loadComponentForEntity(movingEntity, CanMoveComponent.class);
		SpeedComponent sc = entities.loadComponentForEntity(movingEntity, SpeedComponent.class);

		sc = setSpeeds(sc, mc, cmc);

		if (isEntityStationary(sc)) {
			entities.removeComponentsFromEntity(movingEntity, sc, mc);
		} else {
			moveEntity(movingEntity, tpf, sc);
		}
	}

	private void moveEntity(Entity movingEntity, float tpf, SpeedComponent sc) {
		LocationComponent lc = entities.loadComponentForEntity(movingEntity, LocationComponent.class);

		Vector3f location = lc.getTranslation();
		Quaternion rotation = lc.getRotation();

		location = moveEntity(sc.getMoveSpeed() * tpf, location, rotation);
		location = strafeEntity(sc.getStrafeSpeed() * tpf, location, rotation);
		rotateEntity(sc.getRotationSpeed() * tpf, rotation);
		// TODO: figure out if this check is actually needed. In theory, it
		// should be enough to just alter the location. The SceneAppState should
		// convert that into a screen location.
		if (onScreen(location)) {
			entities.changeComponentsForEntity(movingEntity, sc, createLocationComponent(location, rotation, lc));
		} else {
			entities.changeComponentsForEntity(movingEntity, sc,
					createLocationComponent(lc.getTranslation(), rotation, lc));
		}
	}

	private boolean isEntityStationary(SpeedComponent sc) {
		return sc.getMoveSpeed() == 0 && sc.getStrafeSpeed() == 0 && sc.getRotationSpeed() == 0;
	}

	private Vector3f moveEntity(float movementSpeed, Vector3f location, Quaternion rotation) {
		if (hasSpeed(movementSpeed)) {
			return location.add(rotation.mult(Vector3f.UNIT_Z).normalizeLocal().multLocal(movementSpeed));
		}
		return location;
	}

	private Vector3f strafeEntity(float strafeSpeed, Vector3f location, Quaternion rotation) {
		if (hasSpeed(strafeSpeed)) {
			return location.add(rotation.mult(Vector3f.UNIT_X).normalizeLocal().multLocal(strafeSpeed));
		}
		return location;
	}

	private void rotateEntity(float rotateSpeed, Quaternion rotation) {
		if (hasSpeed(rotateSpeed)) {
			Quaternion rot = new Quaternion();
			rot.fromAngles(0f, rotateSpeed, 0f);
			rotation.multLocal(rot);
		}
	}

	private boolean hasSpeed(float movementSpeed) {
		return Float.compare(movementSpeed, 0.0f) != 0;
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
		float speed = movementSpeed + cmc.getAcceleration();
		if (speed > cmc.getMaxSpeed()) {
			speed = cmc.getMaxSpeed();
		}
		return speed;
	}

	private float moveBackwards(float movementSpeed, CanMoveComponent cmc) {
		float speed = movementSpeed - cmc.getBrake();
		if (speed < -cmc.getMaxSpeed()) {
			speed = -cmc.getMaxSpeed();
		}
		return speed;
	}

	private float decelerateMovement(float movementSpeed, CanMoveComponent cmc) {
		float speed = movementSpeed - cmc.getDeceleration();
		if (speed < 0) {
			speed = 0;
		}
		return speed;
	}

	private float accelerateMovement(float movementSpeed, CanMoveComponent cmc) {
		float speed = movementSpeed + cmc.getDeceleration();
		if (speed > 0) {
			speed = 0;
		}
		return speed;
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
		float speed = strafeSpeed + cmc.getAcceleration();
		if (speed > cmc.getMaxSpeed()) {
			speed = cmc.getMaxSpeed();
		}
		return speed;
	}

	private float strafeRight(float strafeSpeed, CanMoveComponent cmc) {
		float speed = strafeSpeed - cmc.getBrake();
		if (speed < -cmc.getMaxSpeed()) {
			speed = -cmc.getMaxSpeed();
		}
		return speed;
	}

	private float decelerateStrafing(float strafeSpeed, float deceleration) {
		float speed = strafeSpeed - deceleration;
		if (speed < 0) {
			speed = 0;
		}
		return speed;
	}

	private float accelerateStrafing(float strafeSpeed, float deceleration) {
		float speed = strafeSpeed + deceleration;
		if (speed > 0) {
			speed = 0;
		}
		return speed;
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
		Vector3f screenLocation = getApplication().getCamera().getScreenCoordinates(location);
		if (screenLocation.x > getApplication().getContext().getSettings().getWidth() || screenLocation.x < 0) {
			onScreen = false;
		}
		if (screenLocation.y > getApplication().getContext().getSettings().getHeight() || screenLocation.y < 0) {
			onScreen = false;
		}
		return onScreen;
	}
}

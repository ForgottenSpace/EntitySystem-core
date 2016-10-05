package com.ractoc.fs.components.ai;

import static com.ractoc.fs.components.ai.AiConstants.DESTROYED_EXIT;
import static com.ractoc.fs.components.ai.AiConstants.APPROACH_RANGE_PROPERTY;

import java.util.List;

import com.forgottenspace.ai.AiComponent;
import com.forgottenspace.es.ComponentTypeCriteria;
import com.forgottenspace.es.Entities;
import com.forgottenspace.es.Entity;
import com.forgottenspace.es.EntityResultSet;
import com.forgottenspace.es.components.CanMoveComponent;
import com.forgottenspace.es.components.ControlledComponent;
import com.forgottenspace.es.components.LocationComponent;
import com.forgottenspace.es.components.MovementComponent;
import com.forgottenspace.es.components.SpeedComponent;
import com.forgottenspace.parsers.ai.AiComponentExit;
import com.forgottenspace.parsers.ai.AiComponentProperty;
import com.jme3.math.Vector3f;

public class ApproachComponent extends AiComponent {

    @AiComponentProperty(name = APPROACH_RANGE_PROPERTY, displayName = "Range", type = Float.class, shortDescription = "Minimum range between the AI and the player.")
    private Float range;
    @AiComponentExit(name = DESTROYED_EXIT, displayName = "Destroyed", type = String.class, shortDescription = "The ship is destroyed.")
    private String destroyed;
    
    private Entities entities = Entities.getInstance();
	private EntityResultSet controlledResultSet;
    private Entity controlledEntity;
    private Entity shipEntity;
    private MovementComponent movementComponent;

    public ApproachComponent(String id) {
        super(id);
        queryControlledResultSet();
    }

	private void queryControlledResultSet() {
        ComponentTypeCriteria criteria = new ComponentTypeCriteria(LocationComponent.class, ControlledComponent.class);
        controlledResultSet = entities.queryEntities(criteria);
    }

    @Override
    public String[] getMandatoryProperties() {
        return new String[]{APPROACH_RANGE_PROPERTY};
    }

    @Override
    public String[] getMandatoryExits() {
        return new String[]{DESTROYED_EXIT};
    }

    @Override
    public void initialiseProperties() {
        range = Float.valueOf((String) getProp(APPROACH_RANGE_PROPERTY));
        destroyed = (String) exits.get(DESTROYED_EXIT);
    }

    @Override
    public void updateProperties() {
        props.put(APPROACH_RANGE_PROPERTY, range.toString());
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        determineControlledEntity();
        shipEntity = entities.getEntityById((Long) getProp("shipEntity"));
        if (shipEntity != null) {
            LocationComponent shipLocationComponent = entities.loadComponentForEntity(shipEntity, LocationComponent.class);
            if (controlledEntity != null && shipLocationComponent != null) {
                LocationComponent controlledLocationComponent = entities.loadComponentForEntity(controlledEntity, LocationComponent.class);
                turnTowardsControlledEntity(shipLocationComponent, controlledLocationComponent);
                moveTowardsControlledEntity(shipLocationComponent, controlledLocationComponent);
            }
        } else {
            aiScript.setCurrentComponent(destroyed);
        }
    }

    private float determineTurnDirection(LocationComponent playerLocationComponent, LocationComponent controlledLocationComponent) {
        Vector3f relPosition = controlledLocationComponent.getTranslation().subtract(playerLocationComponent.getTranslation()).normalize();
        Vector3f myDirection = playerLocationComponent.getRotation().mult(Vector3f.UNIT_Z).normalize();
        Vector3f myLeftDirection = myDirection.cross(Vector3f.UNIT_Y).normalize();
        return myLeftDirection.dot(relPosition);
    }

    private void determineControlledEntity() {
        EntityResultSet.UpdateProcessor updateProcessor = controlledResultSet.getUpdateProcessor();
        updateRemovedEntities(updateProcessor.getRemovedEntities());
        updateAddedEntities(updateProcessor.getAddedEntities());
        updateProcessor.finalizeUpdates();
    }

    private void updateRemovedEntities(List<Entity> removedEntities) {
        if (!removedEntities.isEmpty()) {
            controlledEntity = null;
        }
    }

    private void updateAddedEntities(List<Entity> addedEntities) {
        if (!addedEntities.isEmpty()) {
            controlledEntity = addedEntities.get(0);
        }
    }

    private void rotateLeft() {
        entities.changeComponentsForEntity(shipEntity, new MovementComponent(
                movementComponent.isMoveForward(),
                movementComponent.isMoveBackwards(),
                movementComponent.isStrafeLeft(),
                movementComponent.isStrafeRight(),
                true,
                false));
    }

    private void rotateRight() {
        entities.changeComponentsForEntity(shipEntity, new MovementComponent(
                movementComponent.isMoveForward(),
                movementComponent.isMoveBackwards(),
                movementComponent.isStrafeLeft(),
                movementComponent.isStrafeRight(),
                false,
                true));
    }

    private void moveForward() {
        entities.changeComponentsForEntity(shipEntity, new MovementComponent(
                true,
                false,
                movementComponent.isStrafeLeft(),
                movementComponent.isStrafeRight(),
                movementComponent.isRotateLeft(),
                movementComponent.isRotateRight()));
    }

    private void stopMoving() {
        entities.changeComponentsForEntity(shipEntity, new MovementComponent(
                false,
                false,
                movementComponent.isStrafeLeft(),
                movementComponent.isStrafeRight(),
                movementComponent.isRotateLeft(),
                movementComponent.isRotateRight()));
    }

	private boolean entityHasMovementComponent() {
        return shipEntity.matches(new ComponentTypeCriteria(MovementComponent.class));
    }

	private boolean entityHasSpeedComponent() {
        return shipEntity.matches(new ComponentTypeCriteria(SpeedComponent.class));
    }

    private void loadMovementComponent() {
        if (entityHasMovementComponent()) {
            movementComponent = entities.loadComponentForEntity(shipEntity, MovementComponent.class);
        } else {
            setDefaultComponents();
        }
    }

    private void setDefaultComponents() {
        movementComponent = new MovementComponent(false, false, false, false, false, false);
        entities.addComponentsToEntity(shipEntity, movementComponent);
        entities.addComponentsToEntity(shipEntity, new SpeedComponent(0F, 0F, 0F));
    }

    private void stopRotating() {
        entities.changeComponentsForEntity(shipEntity, new MovementComponent(
                movementComponent.isMoveForward(),
                movementComponent.isMoveBackwards(),
                movementComponent.isStrafeLeft(),
                movementComponent.isStrafeRight(),
                false,
                false));
    }

    private void turnTowardsControlledEntity(LocationComponent playerLocationComponent, LocationComponent controlledLocationComponent) {
        loadMovementComponent();
        float turn = determineTurnDirection(playerLocationComponent, controlledLocationComponent);
        if (turn < 0) {
            rotateLeft();
        } else if (turn > 0) {
            rotateRight();
        } else {
            stopRotating();
        }
    }

    private void moveTowardsControlledEntity(LocationComponent playerLocationComponent, LocationComponent controlledLocationComponent) {
        loadMovementComponent();
        if (determineBrakingDistance(playerLocationComponent, controlledLocationComponent) > range) {
            moveForward();
        } else {
            stopMoving();
        }
    }

    private float determineBrakingDistance(LocationComponent shipLocationComponent, LocationComponent controlledLocationComponent) {
        float distance = shipLocationComponent.getTranslation().distance(controlledLocationComponent.getTranslation());
        float distanceTraveled = 0f;
        if (entityHasSpeedComponent()) {
            SpeedComponent controlledSpeedComponent = entities.loadComponentForEntity(shipEntity, SpeedComponent.class);
            CanMoveComponent controlledCanMoveComponent = entities.loadComponentForEntity(shipEntity, CanMoveComponent.class);
            float startingVelocity = controlledSpeedComponent.getMoveSpeed();
            float deceleration = controlledCanMoveComponent.getBrake();
            float averageVelocity = startingVelocity / 2;
            float brakingTime = startingVelocity / deceleration;
            distanceTraveled = averageVelocity * brakingTime;
        }
        return distance - distanceTraveled;
    }
}

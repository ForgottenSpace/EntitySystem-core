package com.forgottenspace.es.components;

import com.forgottenspace.es.EntityComponent;
import com.forgottenspace.parsers.entitytemplate.annotation.Template;

@Template(parser = "com.forgottenspace.es.components.parsers.generated.CanMoveComponentParser",
          writer = "com.forgottenspace.es.components.parsers.generated.CanMoveComponentWriter")
public class CanMoveComponent implements EntityComponent {
	

    private Float maxSpeed;
    private Float acceleration;
    private Float deceleration;
    private Float brake;
    private Float turnSpeed;

    public CanMoveComponent(Float maxSpeed, Float acceleration, Float deceleration, Float brake, Float turnSpeed) {
        this.maxSpeed = maxSpeed;
        this.acceleration = acceleration;
        this.deceleration = deceleration;
        this.brake = brake;
        this.turnSpeed = turnSpeed;
    }

    public Float getMaxSpeed() {
        return maxSpeed;
    }

    public Float getAcceleration() {
        return acceleration;
    }

    public Float getDeceleration() {
        return deceleration;
    }

    public Float getBrake() {
        return brake;
    }

    public Float getTurnSpeed() {
        return turnSpeed;
    }
}

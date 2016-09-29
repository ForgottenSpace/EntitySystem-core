package com.ractoc.fs.components.es;

import com.ractoc.fs.es.EntityComponent;
import com.ractoc.fs.parsers.entitytemplate.annotation.Template;

@Template(parser = "com.ractoc.fs.components.parsers.CanMoveComponentParser",
          writer = "com.ractoc.fs.components.parsers.CanMoveComponentWriter")
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

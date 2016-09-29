package com.ractoc.fs.components.es;

import com.ractoc.fs.es.EntityComponent;

public class SpeedComponent implements EntityComponent {

    private final Float moveSpeed;
    private final Float strafeSpeed;
    private final Float rotationSpeed;

    public SpeedComponent(Float moveSpeed, Float strafeSpeed, Float rotationSpeed) {
        this.moveSpeed = moveSpeed;
        this.strafeSpeed = strafeSpeed;
        this.rotationSpeed = rotationSpeed;
    }

    public Float getMoveSpeed() {
        return moveSpeed;
    }

    public Float getStrafeSpeed() {
        return strafeSpeed;
    }

    public Float getRotationSpeed() {
        return rotationSpeed;
    }
}

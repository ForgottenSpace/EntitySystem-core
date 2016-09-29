package com.ractoc.fs.components.es;

import com.ractoc.fs.es.EntityComponent;

public class MovementComponent implements EntityComponent {

    private final boolean moveForward;
    private final boolean moveBackwards;
    private final boolean strafeLeft;
    private final boolean strafeRight;
    private final boolean rotateLeft;
    private final boolean rotateRight;

    public MovementComponent(boolean moveForward, boolean moveBackwards, boolean strafeLeft, boolean strafeRight, boolean rotateLeft, boolean rotateRight) {
        this.moveForward = moveForward;
        this.moveBackwards = moveBackwards;
        this.strafeLeft = strafeLeft;
        this.strafeRight = strafeRight;
        this.rotateLeft = rotateLeft;
        this.rotateRight = rotateRight;
    }

    public boolean isMoveForward() {
        return moveForward;
    }

    public boolean isMoveBackwards() {
        return moveBackwards;
    }

    public boolean isStrafeLeft() {
        return strafeLeft;
    }

    public boolean isStrafeRight() {
        return strafeRight;
    }

    public boolean isRotateLeft() {
        return rotateLeft;
    }

    public boolean isRotateRight() {
        return rotateRight;
    }
}

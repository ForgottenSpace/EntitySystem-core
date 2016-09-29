package com.ractoc.fs.components.es;

public enum Controls {

    MOVE_FORWARD("MOVE_FORWARD"),
    MOVE_BACKWARDS("MOVE_BACKWARDS"),
    STRAFE_RIGHT("STRAFE_RIGHT"),
    STRAFE_LEFT("STRAFE_LEFT"),
    ROTATE_LEFT("ROTATE_LEFT"),
    ROTATE_RIGHT("ROTATE_RIGHT"),
    SHOOT_MAIN("SHOOT_MAIN");
    private final String name;

    Controls(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

package com.forgottenspace.es.components;

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
    
    public static Controls getControlByName(String name) {
    	for (Controls control : values()) {
    		if (control.name.equals(name)) {
    			return control;
    		}
    	}
    	throw new ControlsException("Control for name " + name + "not found");
    }
}

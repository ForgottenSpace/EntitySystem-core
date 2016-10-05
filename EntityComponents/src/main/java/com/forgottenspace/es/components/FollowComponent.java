package com.forgottenspace.es.components;

import com.forgottenspace.es.EntityComponent;
import com.jme3.math.Vector3f;

public class FollowComponent implements EntityComponent {
    private Vector3f location;

    public FollowComponent(Vector3f location) {
        this.location = location;
    }

    public Vector3f getLocation() {
        return location;
    }
    
}

package com.ractoc.fs.components.es;

import com.jme3.math.Vector3f;
import com.ractoc.fs.es.EntityComponent;

public class FollowComponent implements EntityComponent {
    private Vector3f location;

    public FollowComponent(Vector3f location) {
        this.location = location;
    }

    public Vector3f getLocation() {
        return location;
    }
    
}

package com.forgottenspace.es.components;

import com.forgottenspace.es.EntityComponent;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

public class LocationComponent implements EntityComponent {

    private final Vector3f translation;
    private final Quaternion rotation;
    private final Vector3f scale;

    public LocationComponent(Vector3f translation, Quaternion rotation, Vector3f scale) {
        this.translation = translation;
        this.rotation = rotation;
        this.scale = scale;
    }

    public Vector3f getTranslation() {
        return translation.clone();
    }

    public Quaternion getRotation() {
        return rotation.clone();
    }

    public Vector3f getScale() {
        return scale.clone();
    }
}

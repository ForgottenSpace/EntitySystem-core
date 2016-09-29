/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ractoc.fs.components.es;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.ractoc.fs.es.EntityComponent;

/**
 *
 */
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

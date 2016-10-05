package com.forgottenspace.es.components;

import com.forgottenspace.es.EntityComponent;

public class ShootMainComponent implements EntityComponent {

    private final float interval;

    public ShootMainComponent(float interval) {
        this.interval = interval;
    }

    public float getInterval() {
        return interval;
    }
}

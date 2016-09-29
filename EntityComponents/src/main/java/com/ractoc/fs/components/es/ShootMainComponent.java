package com.ractoc.fs.components.es;

import com.ractoc.fs.es.EntityComponent;

public class ShootMainComponent implements EntityComponent {

    private final float interval;

    public ShootMainComponent(float interval) {
        this.interval = interval;
    }

    public float getInterval() {
        return interval;
    }
}

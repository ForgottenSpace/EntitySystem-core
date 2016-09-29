package com.ractoc.fs.components.es;

import com.ractoc.fs.es.EntityComponent;

public class DamageComponent implements EntityComponent {

    private final Integer damage;

    public DamageComponent(Integer damage) {
        this.damage = damage;
    }

    public Integer getDamage() {
        return damage;
    }
}

package com.ractoc.fs.components.es;

import com.ractoc.fs.es.EntityComponent;
import com.ractoc.fs.parsers.entitytemplate.annotation.Template;

@Template(parser = "com.ractoc.fs.components.parsers.StructureComponentParser",
          writer = "com.ractoc.fs.components.parsers.StructureComponentWriter")
public class StructureComponent implements EntityComponent {

    private final Integer hitpoints;

    public StructureComponent(Integer hitpoints) {
        this.hitpoints = hitpoints;
    }

    public Integer getHitpoints() {
        return hitpoints;
    }
}

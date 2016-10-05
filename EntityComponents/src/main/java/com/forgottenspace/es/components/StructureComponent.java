package com.forgottenspace.es.components;

import com.forgottenspace.es.EntityComponent;
import com.forgottenspace.parsers.entitytemplate.annotation.Template;

@Template(parser = "com.forgottenspace.es.components.parsers.generated.StructureComponentParser",
          writer = "com.forgottenspace.es.components.parsers.generated.StructureComponentWriter")
public class StructureComponent implements EntityComponent {

    private final Integer hitpoints;

    public StructureComponent(Integer hitpoints) {
        this.hitpoints = hitpoints;
    }

    public Integer getHitpoints() {
        return hitpoints;
    }
}

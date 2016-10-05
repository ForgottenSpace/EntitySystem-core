package com.forgottenspace.es.components;

import com.forgottenspace.es.EntityComponent;
import com.forgottenspace.parsers.entitytemplate.annotation.Template;

@Template(parser = "com.forgottenspace.es.components.parsers.generated.RenderComponentParser",
          writer = "com.forgottenspace.es.components.parsers.generated.RenderComponentWriter",
          model = "j3o")
public class RenderComponent implements EntityComponent {

    private String j3o;

    public RenderComponent(String j3o) {
        this.j3o = j3o;
    }

    public String getJ3o() {
        return j3o;
    }
}

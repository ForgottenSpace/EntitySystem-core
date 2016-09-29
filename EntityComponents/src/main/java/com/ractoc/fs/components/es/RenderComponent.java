package com.ractoc.fs.components.es;

import com.ractoc.fs.es.EntityComponent;
import com.ractoc.fs.parsers.entitytemplate.annotation.Template;

@Template(parser = "com.ractoc.fs.components.parsers.RenderComponentParser",
          writer = "com.ractoc.fs.components.parsers.RenderComponentWriter",
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

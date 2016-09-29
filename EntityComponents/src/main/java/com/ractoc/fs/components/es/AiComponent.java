package com.ractoc.fs.components.es;

import com.ractoc.fs.es.EntityComponent;
import com.ractoc.fs.parsers.entitytemplate.annotation.Template;

@Template(parser = "com.ractoc.fs.components.parsers.AiComponentParser",
        writer = "com.ractoc.fs.components.parsers.AiComponentWriter",
        proxyColor = "RED")
public class AiComponent implements EntityComponent {
    private final String script;

    public AiComponent(String script) {
        this.script = script;
    }

    public String getScript() {
        return script;
    }

}

package com.forgottenspace.es.components;

import com.forgottenspace.es.EntityComponent;
import com.forgottenspace.parsers.entitytemplate.annotation.Template;

@Template(parser = "com.forgottenspace.es.components.parsers.generated.AiComponentParser",
        writer = "com.forgottenspace.es.components.parsers.generated.AiComponentWriter",
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
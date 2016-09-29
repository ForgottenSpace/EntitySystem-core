package com.ractoc.fs.parsers.entitytemplate;

import com.ractoc.fs.es.EntityComponent;
import com.ractoc.fs.parsers.ParserException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class ComponentParser {

    protected Map<String, String> properties = new HashMap<String, String>();
    protected final List<String> mandatoryProperties = new ArrayList<String>();

    public void setProperty(String key, String value) {
        if (mandatoryProperties.contains(key)) {
            setValidProperty(key, value);
        } else {
            throw new ParserException("Property not allowed on " + this.getClass().getName() + ": " + key);
        }
    }

    private void setValidProperty(String key, String value) {
        if (properties.containsKey(key)) {
            throw new ParserException(this.getClass().getName() + ": Property already added: " + key);
        } else {
            properties.put(key, value);
        }
    }

    public EntityComponent getParsedComponent() {
        if (testAllPropertiesSet()) {
            return createComponent();
        } else {
            throw new ParserException("Not all mandatory properties set for parser " + this.getClass().getName() + ".");
        }
    }

    private boolean testAllPropertiesSet() {
        Set<String> setPropertyKeys = properties.keySet();
        return setPropertyKeys.containsAll(mandatoryProperties);
    }

    public void clear() {
        properties.clear();
    }

    protected abstract EntityComponent createComponent();
}

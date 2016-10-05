package com.forgottenspace.parsers.ai;

import com.forgottenspace.ai.AiComponent;
import com.forgottenspace.ai.AiScript;
import com.forgottenspace.parsers.ParserException;
import com.forgottenspace.parsers.entitytemplate.KeyValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class AiScriptParser {
	private static final String CLASS_KEY = "class";
	private static final String COMPONENT_KEY = "component";
	private static final String COMPONENTS_KEY = "components";
	private static final String ENTRY_KEY = "entry";
	private static final String EXITS_KEY = "exits";
	private static final String ID_KEY = "id";
	private static final String NAME_KEY = "name";
	private static final String PROPERTIES_KEY = "properties";
	private static final String SCRIPT_KEY = "script";
	
	private static final String KEY_VALUE_SEPARATOR = "=";
	
	private static final String BLOCK_END = "}";

	public static final String UNABLE_TO_PARSE_SCRIPT_FILE = "Unable to parse script file.";

    private AiScript script;
    private ClassLoader loader;

    public AiScriptParser() {
        loader = this.getClass().getClassLoader();
    }

    public AiScript parse(InputStream scriptStream) {
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(scriptStream))) {
            String line = reader.readLine();
            while (line != null) {
                line = line.trim();
                if (line.startsWith(SCRIPT_KEY)) {
                    parseScript(reader);
                }
                line = reader.readLine();
            }
        } catch (IOException | InstantiationException | IllegalAccessException | NoSuchMethodException | IllegalArgumentException | InvocationTargetException ex) {
            throw new ParserException(UNABLE_TO_PARSE_SCRIPT_FILE, ex);
        }
        return script;
    }

    private void parseScript(BufferedReader reader) throws IOException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        String line = reader.readLine();
        while (line != null) {
            line = line.trim();
            if (line.equals(BLOCK_END)) {
                return;
            } else if (line.startsWith(NAME_KEY)) {
                KeyValue kv = getKeyValuePair(line);
                String name = kv.getValue();
                script = new AiScript(name);
            } else if (line.startsWith(ENTRY_KEY)) {
                KeyValue kv = getKeyValuePair(line);
                String entry = kv.getValue();
                script.setEntry(entry);
            } else if (line.startsWith(COMPONENTS_KEY)) {
                parseComponents(reader);
            }
            line = reader.readLine();
        }
    }

    private KeyValue getKeyValuePair(String keyValueString) {
        String[] keyValue = keyValueString.split(KEY_VALUE_SEPARATOR);
        String key = keyValue[0].trim();
        String value = keyValue[1].trim();
        return new KeyValue(key, value);
    }

    private void parseComponents(BufferedReader reader) throws IOException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        String line = reader.readLine();
        while (line != null) {
            line = line.trim();
            if (line.equals(BLOCK_END)) {
                return;
            } else if (line.startsWith(COMPONENT_KEY)) {
                parseComponent(reader);
            }
            line = reader.readLine();
        }
    }

    private void parseComponent(BufferedReader reader) throws IOException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        AiComponent component = null;
        String line = reader.readLine();
        String componentClassName = null;
        Map<String, Object> props = null;
        Map<String, Object> exits = null;
        while (line != null) {
            line = line.trim();
            if (line.equals(BLOCK_END)) {
                if (component != null) {
                    component.configure(props, exits);
                    script.addComponent(component);
                }
                return;
            } else if (line.startsWith(CLASS_KEY)) {
                KeyValue kv = getKeyValuePair(line);
                componentClassName = kv.getValue();
            } else if (line.startsWith(ID_KEY)) {
                KeyValue kv = getKeyValuePair(line);
                String id = kv.getValue();
                Class<? extends AiComponent> componentType = getComponentTypeForClassName(componentClassName);
                component = componentType.getConstructor(String.class).newInstance(id);
            } else if (line.startsWith(PROPERTIES_KEY)) {
                props = parseProperties(reader);
            } else if (line.startsWith(EXITS_KEY)) {
                exits = parseProperties(reader);
            }
            line = reader.readLine();
        }
    }

    @SuppressWarnings("unchecked")
	private Class<? extends AiComponent> getComponentTypeForClassName(String className) {
        try {
            return (Class<? extends AiComponent>) loader.loadClass(className);
        } catch (ClassNotFoundException ex) {
            throw new ParserException(className + " is not an AIComponent", ex);
        }
    }

    private Map<String, Object> parseProperties(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        Map<String, Object> props = new HashMap<>();
        while (line != null) {
            line = line.trim();
            if (line.equals(BLOCK_END)) {
                break;
            } else {
                KeyValue kv = getKeyValuePair(line);
                props.put(kv.getKey(), kv.getValue());
            }
            line = reader.readLine();
        }
        return props;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.loader = classLoader;
    }
}

package com.ractoc.fs.parsers.ai;

import com.ractoc.fs.ai.AiComponent;
import com.ractoc.fs.ai.AiScript;
import com.ractoc.fs.parsers.ParserException;
import com.ractoc.fs.parsers.entitytemplate.KeyValue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class AiScriptParser {
    public static final String UNABLE_TO_PARSE_SCRIPT_FILE = "Unable to parse script file.";

    private BufferedReader reader;
    private AiScript script;
    private ClassLoader loader;

    public AiScriptParser() {
        loader = this.getClass().getClassLoader();
    }

    public AiScript parse(InputStream scriptStream) {
        try {
            reader = new BufferedReader(new InputStreamReader(scriptStream));
            String line = reader.readLine();
            while (line != null) {
                line = line.trim();
                if (line.startsWith("script")) {
                    parseScript();
                }
                line = reader.readLine();
            }
        } catch (IOException ex) {
            throw new ParserException(UNABLE_TO_PARSE_SCRIPT_FILE, ex);
        } catch (InstantiationException ex) {
            throw new ParserException(UNABLE_TO_PARSE_SCRIPT_FILE, ex);
        } catch (IllegalAccessException ex) {
            throw new ParserException(UNABLE_TO_PARSE_SCRIPT_FILE, ex);
        } catch (NoSuchMethodException ex) {
            throw new ParserException(UNABLE_TO_PARSE_SCRIPT_FILE, ex);
        } catch (IllegalArgumentException ex) {
            throw new ParserException(UNABLE_TO_PARSE_SCRIPT_FILE, ex);
        } catch (InvocationTargetException ex) {
            throw new ParserException(UNABLE_TO_PARSE_SCRIPT_FILE, ex);
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                throw new ParserException("Unable to properly close the script file.", ex);
            }
        }
        return script;
    }

    private void parseScript() throws IOException, InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
        String line = reader.readLine();
        while (line != null) {
            line = line.trim();
            if (line.equals("}")) {
                return;
            } else if (line.startsWith("name")) {
                KeyValue kv = getKeyValuePair(line);
                String name = kv.getValue();
                script = new AiScript(name);
            } else if (line.startsWith("entry")) {
                KeyValue kv = getKeyValuePair(line);
                String entry = kv.getValue();
                script.setEntry(entry);
            } else if (line.startsWith("components")) {
                parseComponents();
            }
            line = reader.readLine();
        }
    }

    private KeyValue getKeyValuePair(String keyValueString) {
        String[] keyValue = keyValueString.split("=");
        String key = keyValue[0].trim();
        String value = keyValue[1].trim();
        return new KeyValue(key, value);
    }

    private void parseComponents() throws IOException, InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
        String line = reader.readLine();
        while (line != null) {
            line = line.trim();
            if (line.equals("}")) {
                return;
            } else if (line.startsWith("component")) {
                parseComponent();
            }
            line = reader.readLine();
        }
    }

    private void parseComponent() throws IOException, InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
        AiComponent component = null;
        String line = reader.readLine();
        String componentClassName = null;
        Map<String, Object> props = null;
        Map<String, Object> exits = null;
        while (line != null) {
            line = line.trim();
            if (line.equals("}")) {
                if (component != null) {
                    component.configure(props, exits);
                    script.addComponent(component);
                }
                return;
            } else if (line.startsWith("class")) {
                KeyValue kv = getKeyValuePair(line);
                componentClassName = kv.getValue();
            } else if (line.startsWith("id")) {
                KeyValue kv = getKeyValuePair(line);
                String id = kv.getValue();
                Class<? extends AiComponent> componentType = getComponentTypeForClassName(componentClassName);
                component = componentType.getConstructor(String.class).newInstance(id);
            } else if (line.startsWith("properties")) {
                props = parseProperties();
            } else if (line.startsWith("exits")) {
                exits = parseProperties();
            }
            line = reader.readLine();
        }
    }

    private Class<? extends AiComponent> getComponentTypeForClassName(String className) {
        try {
            System.out.println("loading class " + className);
            Class clazz = loader.loadClass(className);
            System.out.println("is AiComponent: " + AiComponent.class.isAssignableFrom(clazz));
            return (Class<? extends AiComponent>) clazz;
        } catch (ClassNotFoundException ex) {
            throw new ParserException(className + " is not an AIComponent", ex);
        }
    }

    private Map<String, Object> parseProperties() throws IOException {
        String line = reader.readLine();
        Map<String, Object> props = new HashMap<String, Object>();
        while (line != null) {
            line = line.trim();
            if (line.equals("}")) {
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

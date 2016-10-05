package com.forgottenspace.parsers.entitytemplate;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.forgottenspace.es.EntityComponent;
import com.forgottenspace.parsers.ParserException;

public class TemplateParser {

	private static final String CLASS_KEY = "class";
	private static final String COMPONENT_KEY = "component";
	private static final String COMPONENTS_KEY = "components";
	private static final String KEY_VALUE_SEPARATOR = "=";
	private static final String NAME_KEY = "name";
	private static final String PROPERTIES_KEY = "properties";
	private static final String TEMPLATE_KEY = "template";
	
	private static final String END_BLOCK = "}";
	
	private static ComponentParsersCache componentParsers = new ComponentParsersCache();
    
	private EntityTemplate template;
    private ClassLoader classLoader;

    public TemplateParser() {
        classLoader = this.getClass().getClassLoader();
    }

    public EntityTemplate parse(String templateFile) {
        try(InputStream fis = new FileInputStream(templateFile)) {
            return parse(fis);
        } catch (IOException ex) {
            throw new ParserException("Unable to open file " + templateFile, ex);
        }
    }

    public EntityTemplate parse(InputStream templateStream) {
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(templateStream))) {
            String line = reader.readLine();
            while (line != null) {
                line = line.trim();
                if (line.startsWith(TEMPLATE_KEY)) {
                    parseTemplate(reader);
                }
                line = reader.readLine();
            }
        } catch (IOException ex) {
            throw new ParserException("Unable to parse template file.", ex);
        }
        return template;
    }

    private void parseTemplate(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        while (line != null) {
            line = line.trim();
            if (line.equals(END_BLOCK)) {
                return;
            } else if (line.startsWith(NAME_KEY)) {
                KeyValue kv = getKeyValuePair(line);
                String name = kv.getValue();
                template = new EntityTemplate(name);
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

    private void parseComponents(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        while (line != null) {
            line = line.trim();
            if (line.equals(END_BLOCK)) {
                return;
            } else if (line.startsWith(COMPONENT_KEY)) {
                parseComponent(reader);
            }
            line = reader.readLine();
        }
    }

    private void parseComponent(BufferedReader reader) throws IOException {
        ComponentParser parser = null;
        String line = reader.readLine();
        while (line != null) {
            line = line.trim();
            if (line.equals(END_BLOCK) && parser != null) {
                EntityComponent component = parser.getParsedComponent();
                if (component != null) {
                    template.addComponent(component);
                }
                return;
            } else if (line.startsWith(CLASS_KEY)) {
                KeyValue kv = getKeyValuePair(line);
                String componentClassName = kv.getValue();
                Class<? extends EntityComponent> componentType = getComponentTypeForClassName(componentClassName);
                parser = componentParsers.getParserForComponentType(componentType);
            } else if (line.startsWith(PROPERTIES_KEY) && parser != null) {
                parsePropertiesWithComponentParser(reader, parser);
            }
            line = reader.readLine();
        }
    }

    @SuppressWarnings("unchecked")
	private Class<? extends EntityComponent> getComponentTypeForClassName(String className) {
        try {
            return (Class<? extends EntityComponent>) classLoader.loadClass(className);
        } catch (ClassNotFoundException ex) {
            throw new ParserException(className + " is not an EntityComponent", ex);
        }
    }

    private void parsePropertiesWithComponentParser(BufferedReader reader, ComponentParser parser) throws IOException {
        String line = reader.readLine();
        parser.clear();
        while (line != null) {
            line = line.trim();
            if (line.equals(END_BLOCK)) {
                return;
            } else if (line.length() > 0) {
                KeyValue kv = getKeyValuePair(line);
                parser.setProperty(kv.getKey(), kv.getValue());
            }
            line = reader.readLine();
        }
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
        componentParsers.setClassLoader(classLoader);
    }
}

package com.ractoc.fs.parsers.entitytemplate;

import com.ractoc.fs.es.EntityComponent;
import com.ractoc.fs.parsers.ParserException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TemplateParser {

    private static ComponentParsersCache componentParsers = new ComponentParsersCache();
    private BufferedReader reader;
    private EntityTemplate template;
    private ClassLoader classLoader;

    public TemplateParser() {
        classLoader = this.getClass().getClassLoader();
    }

    public EntityTemplate parse(String templateFile) {
        try {
            return parse(new FileInputStream(templateFile));
        } catch (FileNotFoundException ex) {
            throw new ParserException("Unable to open file " + templateFile, ex);
        }
    }

    public EntityTemplate parse(InputStream templateStream) {
        try {
            reader = new BufferedReader(new InputStreamReader(templateStream));
            String line = reader.readLine();
            while (line != null) {
                line = line.trim();
                if (line.startsWith("template")) {
                    parseTemplate();
                }
                line = reader.readLine();
            }
        } catch (IOException ex) {
            throw new ParserException("Unable to parse template file.", ex);
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                throw new ParserException("Unable to properly close the template file.", ex);
            }
        }
        return template;
    }

    private void parseTemplate() throws IOException {
        String line = reader.readLine();
        while (line != null) {
            line = line.trim();
            if (line.equals("}")) {
                return;
            } else if (line.startsWith("name")) {
                KeyValue kv = getKeyValuePair(line);
                String name = kv.getValue();
                template = new EntityTemplate(name);
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

    private void parseComponents() throws IOException {
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

    private void parseComponent() throws IOException {
        ComponentParser parser = null;
        String line = reader.readLine();
        while (line != null) {
            line = line.trim();
            if (line.equals("}")) {
                EntityComponent component = parser.getParsedComponent();
                if (component != null) {
                    template.addComponent(component);
                }
                return;
            } else if (line.startsWith("class")) {
                KeyValue kv = getKeyValuePair(line);
                String componentClassName = kv.getValue();
                Class<? extends EntityComponent> componentType = getComponentTypeForClassName(componentClassName);
                parser = componentParsers.getParserForComponentType(componentType);
            } else if (line.startsWith("properties")) {
                parsePropertiesWithComponentParser(parser);
            }
            line = reader.readLine();
        }
    }

    private Class<? extends EntityComponent> getComponentTypeForClassName(String className) {
        try {
            return (Class<? extends EntityComponent>) classLoader.loadClass(className);
        } catch (ClassNotFoundException ex) {
            throw new ParserException(className + " is not an EntityComponent", ex);
        }
    }

    private void parsePropertiesWithComponentParser(ComponentParser parser) throws IOException {
        String line = reader.readLine();
        parser.clear();
        while (line != null) {
            line = line.trim();
            if (line.equals("}")) {
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

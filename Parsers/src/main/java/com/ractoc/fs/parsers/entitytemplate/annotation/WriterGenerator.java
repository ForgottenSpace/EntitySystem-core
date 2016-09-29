package com.ractoc.fs.parsers.entitytemplate.annotation;

import java.io.IOException;
import java.io.Writer;

public class WriterGenerator {

    private Writer writer;
    private String writerPackageName;
    private String writerClassName;
    private String componentClassName;
    private PropertyType[] properties;

    public WriterGenerator(String writerName, String componentClassName, PropertyType[] properties) {
        writerPackageName = writerName.substring(0, writerName.lastIndexOf("."));
        writerClassName = writerName.substring(writerName.lastIndexOf(".") + 1);
        this.componentClassName = componentClassName;
        this.properties = properties;
    }

    public void generate(Writer writer) throws IOException {
        this.writer = writer;
        generateHeader();
        generateConstructor();
        generatePullPropertiesFromComponent();
        generateFooter();
    }

    private void generateHeader() throws IOException {
        writer.write("package " + writerPackageName + ";\n");
        writer.write("\n");
        writer.write("import com.ractoc.fs.es.EntityComponent;\n");
        writer.write("import com.ractoc.fs.parsers.entitytemplate.ComponentWriter;\n");
        writer.write("\n");
        writer.write("public class " + writerClassName + " extends ComponentWriter {\n");
        writer.write("\n");
    }

    private void generateConstructor() throws IOException {
        writer.write("\tpublic " + writerClassName + "() {\n");
        for (PropertyType property : properties) {
            writer.write("\t\tmandatoryProperties.add(\"" + property.getFieldName() + "\");\n");
        }
        writer.write("\t}\n");
        writer.write("\n");
    }

    private void generatePullPropertiesFromComponent() throws IOException {
        writer.write("\t@Override\n");
        writer.write("\tprotected void pullPropertiesFromComponent(EntityComponent component) {\n");
        for (PropertyType property : properties) {
            createGetPropertyMethod(property);
        }
        writer.write("\t}\n");
        writer.write("\n");
    }

    private void generateFooter() throws IOException {
        writer.write("}\n");
    }

    private void createGetPropertyMethod(PropertyType property) throws IOException {
        String getMethod = convertPropertyToGetMethod(property.getFieldName());
        writer.write("\t\tproperties.put(\"" + property.getFieldName() + "\", extractValueWithMethod(component, \"" + getMethod + "\"));\n");
    }

    private String convertPropertyToGetMethod(String property) {
        String firstCharacter = property.substring(0, 1);
        String restOfProperty = property.substring(1);
        return "get" + firstCharacter.toUpperCase() + restOfProperty;
    }
}

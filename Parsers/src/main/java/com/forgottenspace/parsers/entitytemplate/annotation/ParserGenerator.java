package com.forgottenspace.parsers.entitytemplate.annotation;

import java.io.IOException;
import java.io.Writer;

public class ParserGenerator {

    private static final String PACKAGE_SEPARATOR = ".";
    
	private Writer writer;
    private String parserPackageName;
    private String parserClassName;
    private String componentClassName;
    private PropertyType[] properties;

    public ParserGenerator(String parserName, String componentClassName, PropertyType[] properties) {
        parserPackageName = parserName.substring(0, parserName.lastIndexOf(PACKAGE_SEPARATOR));
        parserClassName = parserName.substring(parserName.lastIndexOf(PACKAGE_SEPARATOR) + 1);
        this.componentClassName = componentClassName;
        this.properties = properties;
    }

    public void generate(Writer writer) throws IOException {
        this.writer = writer;
        generateHeader();
        generateConstructor();
        generateCreateComponent();
        generateFooter();
    }

    private void generateHeader() throws IOException {
        writer.write("package " + parserPackageName + ";\n");
        writer.write("\n");
        writer.write("import com.forgottenspace.es.EntityComponent;\n");
        writer.write("import com.forgottenspace.parsers.entitytemplate.ComponentParser;\n");
        writer.write("\n");
        writer.write("public class " + parserClassName + " extends ComponentParser {\n");
        writer.write("\n");
    }

    private void generateConstructor() throws IOException {
        writer.write("\tpublic " + parserClassName + "() {\n");
        for (PropertyType property : properties) {
            writer.write("\t\tmandatoryProperties.add(\"" + property.getFieldName() + "\");\n");
        }
        writer.write("\t}\n");
        writer.write("\n");
    }

    private void generateCreateComponent() throws IOException {
        String params = generateConstructorParameters();
        writer.write("\t@Override\n");
        writer.write("\tprotected EntityComponent createComponent() {\n");
        writer.write("\t\treturn new " + componentClassName + "(" + params + ");\n");
        writer.write("\t}\n");
        writer.write("\n");
    }

    private void generateFooter() throws IOException {
        writer.write("}\n");
    }

    private String generateConstructorParameters() {
        StringBuilder constructorParams = new StringBuilder();
        for (PropertyType property : properties) {
            String convert =  createConvertCall(property);
            constructorParams.append(",\n\t\t\t" + convert + "(properties.get(\"");
            constructorParams.append(property.getFieldName());
            constructorParams.append("\"))");
        }
        return constructorParams.substring(5);
    }
    
    private String createConvertCall(PropertyType property) {
        return "new " + property.getFieldClass();
    }
}

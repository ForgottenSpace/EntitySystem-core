package com.forgottenspace.parsers.entitytemplate;

import com.forgottenspace.es.EntityComponent;
import com.forgottenspace.parsers.ParserException;

import java.io.*;
import java.net.URLClassLoader;
import java.util.Map;

public class TemplateWriter {

    private static ComponentWritersCache componentWriters = new ComponentWritersCache();
    private EntityTemplate template;

    public void write(EntityTemplate template, String templateFile) {
        File f = new File(templateFile);
        write(template, f);
    }

    public void write(EntityTemplate template, File templateFile) {
        this.template = template;
        ensureTemplateFileExists(templateFile);
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(templateFile))) {
            writeTemplate(writer);
        } catch (IOException ex) {
            throw new ParserException("Unable to write " + templateFile, ex);
        }
    }

	private void ensureTemplateFileExists(File templateFile) {
		if (!templateFile.exists()) {
            try {
				if (!templateFile.createNewFile()) {
					throw new ParserException("Unable to create the template file.");
				}
			} catch (IOException ex) {
				throw new ParserException("Unable to create the template file.", ex);
			}
        }
	}

    private void writeTemplate(BufferedWriter writer) throws IOException {
        writer.write("template {");
        writer.newLine();
        writer.write("\tname=");
        writer.append(template.getName());
        writer.newLine();
        if (!template.getComponents().isEmpty()) {
            writer.write("\tcomponents {");
            writer.newLine();
            writeComponents(writer);
            writer.write("\t}");
            writer.newLine();
        }
        writer.write("}");
    }

    private void writeComponents(BufferedWriter writer) throws IOException {
        for (EntityComponent component : template.getComponents()) {
            if (component != null) {
                writer.write("\t\tcomponent {");
                writer.newLine();
                writer.write("\t\t\tclass=");
                writer.write(component.getClass().getName());
                writer.newLine();
                writeProperties(writer, component);
                writer.write("\t\t}");
                writer.newLine();
            }
        }
    }

    private void writeProperties(BufferedWriter writer, EntityComponent component) throws IOException {
        ComponentWriter compWriter = componentWriters.getWriterForComponentType(component.getClass());
        Map<String, String> properties = compWriter.getPropertiesFromComponent(component);
        if (properties.size() > 0) {
            writer.write("\t\t\tproperties {");
            writer.newLine();
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                writer.write("\t\t\t\t");
                writer.write(entry.getKey());
                writer.write("=");
                writer.write(entry.getValue());
                writer.newLine();
            }
            writer.write("\t\t\t}");
            writer.newLine();
        }
    }

    public void setLoader(URLClassLoader loader) {
        componentWriters.setClassLoader(loader);
    }
}

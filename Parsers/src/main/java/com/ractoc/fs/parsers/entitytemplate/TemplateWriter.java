package com.ractoc.fs.parsers.entitytemplate;

import com.ractoc.fs.parsers.ParserException;
import com.ractoc.fs.es.EntityComponent;
import java.io.*;
import java.net.URLClassLoader;
import java.util.Map;

public class TemplateWriter {

    private static ComponentWritersCache componentWriters = new ComponentWritersCache();
    private BufferedWriter writer;
    private EntityTemplate template;
    private URLClassLoader loader;

    public void write(EntityTemplate template, String templateFile) {
        File f = new File(templateFile);
        write(template, f);
    }

    public void write(EntityTemplate template, File templateFile) {
        Writer fileWriter = null;
        this.template = template;
        try {
            if (!templateFile.exists()) {
                templateFile.createNewFile();
            }
            fileWriter = new FileWriter(templateFile);
            writer = new BufferedWriter(fileWriter);
            writeTemplate();
        } catch (IOException ex) {
            throw new ParserException("Unable to write " + templateFile, ex);
        } finally {
            try {
                writer.flush();
                writer.close();
            } catch (IOException ex) {
                throw new ParserException("Unable to properly close the template file.", ex);
            }
        }
    }

    private void writeTemplate() throws IOException {
        writer.write("template {");
        writer.newLine();
        writer.write("\tname=");
        writer.append(template.getName());
        writer.newLine();
        if (template.getComponents().size() > 0) {
            writer.write("\tcomponents {");
            writer.newLine();
            writeComponents();
            writer.write("\t}");
            writer.newLine();
        }
        writer.write("}");
    }

    private void writeComponents() throws IOException {
        for (EntityComponent component : template.getComponents()) {
            if (component != null) {
                writer.write("\t\tcomponent {");
                writer.newLine();
                writer.write("\t\t\tclass=");
                writer.write(component.getClass().getName());
                writer.newLine();
                writeProperties(component);
                writer.write("\t\t}");
                writer.newLine();
            }
        }
    }

    private void writeProperties(EntityComponent component) throws IOException {
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
        this.loader = loader;
        componentWriters.setClassLoader(loader);
    }
}

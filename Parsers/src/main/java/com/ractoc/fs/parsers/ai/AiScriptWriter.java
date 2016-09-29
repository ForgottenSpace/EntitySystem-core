package com.ractoc.fs.parsers.ai;

import com.ractoc.fs.ai.AiComponent;
import com.ractoc.fs.ai.AiScript;
import com.ractoc.fs.parsers.ParserException;
import java.io.*;
import java.lang.reflect.Field;

public class AiScriptWriter {

    private AiScript script;
    private BufferedWriter writer;

    public void write(AiScript script, String scriptFile) {
        File f = new File(scriptFile);
        write(script, f);
    }

    public void write(AiScript script, File scriptFile) {
        this.script = script;
        try {
            if (!scriptFile.exists()) {
                scriptFile.createNewFile();
            }
            Writer fileWriter = new FileWriter(scriptFile);
            writer = new BufferedWriter(fileWriter);
            writeScript();
        } catch (IOException ex) {
            throw new ParserException("Unable to write " + scriptFile, ex);
        } catch (IllegalArgumentException ex) {
            throw new ParserException("Unable to write " + scriptFile, ex);
        } catch (IllegalAccessException ex) {
            throw new ParserException("Unable to write " + scriptFile, ex);
        } finally {
            try {
                writer.flush();
                writer.close();
            } catch (IOException ex) {
                throw new ParserException("Unable to properly close the script file.", ex);
            }
        }
    }

    private void writeScript() throws IOException, IllegalArgumentException, IllegalAccessException {
        writer.write("script {");
        writer.newLine();
        writer.write("\tname=");
        writer.append(script.getName());
        writer.newLine();
        writer.write("\tentry=");
        writer.append(script.getEntry());
        writer.newLine();
        if (script.getComponents().size() > 0) {
            writer.write("\tcomponents {");
            writer.newLine();
            writeComponents();
            writer.write("\t}");
            writer.newLine();
        }
        writer.write("}");
    }

    private void writeComponents() throws IOException, IllegalArgumentException, IllegalAccessException {
        for (AiComponent component : script.getComponents()) {
            writeComponent(component);
        }
    }

    private void writeComponent(AiComponent component) throws IllegalAccessException, IOException, IllegalArgumentException {
        if (component != null) {
            writer.write("\t\tcomponent {");
            writer.newLine();
            writer.write("\t\t\tclass=");
            writer.write(component.getClass().getName());
            writer.newLine();
            writer.write("\t\t\tid=");
            writer.write(component.getId());
            writer.newLine();
            writeProperties(component);
            writer.newLine();
            writeExits(component);
            writer.write("\t\t}");
            writer.newLine();
        }
    }

    private void writeProperties(AiComponent component) throws IOException, IllegalArgumentException, IllegalAccessException {
        Field[] fields = component.getClass().getDeclaredFields();
        if (fields != null && fields.length > 0) {
            writer.write("\t\t\tproperties {");
            writer.newLine();
            for (Field field : fields) {
                if (field.getAnnotation(AiComponentProperty.class) != null) {
                    field.setAccessible(true);
                    writer.write("\t\t\t\t");
                    writer.write(field.getAnnotation(AiComponentProperty.class).name());
                    writer.write("=");
                    writer.write(field.get(component).toString());
                    writer.newLine();
                }
            }
            writer.write("\t\t\t}");
            writer.newLine();
        }
    }

    private void writeExits(AiComponent component) throws IOException, IllegalArgumentException, IllegalAccessException {
        Field[] fields = component.getClass().getDeclaredFields();
        if (fields != null && fields.length > 0) {
            writer.write("\t\t\texits {");
            writer.newLine();
            for (Field field : fields) {
                if (field.getAnnotation(AiComponentExit.class) != null) {
                    field.setAccessible(true);
                    writer.write("\t\t\t\t");
                    writer.write(field.getAnnotation(AiComponentExit.class).name());
                    writer.write("=");
                    writer.write(field.get(component).toString());
                    writer.newLine();
                }
            }
            writer.write("\t\t\t}");
            writer.newLine();
        }
    }
}

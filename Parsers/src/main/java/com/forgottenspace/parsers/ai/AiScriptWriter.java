package com.forgottenspace.parsers.ai;

import com.forgottenspace.ai.AiComponent;
import com.forgottenspace.ai.AiScript;
import com.forgottenspace.parsers.ParserException;

import java.io.*;
import java.lang.reflect.Field;

public class AiScriptWriter {

    private AiScript script;

    public void write(AiScript script, String scriptFile) {
        File f = new File(scriptFile);
        write(script, f);
    }

    public void write(AiScript script, File scriptFile) {
        this.script = script;
        ensureScriptFileExists(scriptFile);
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(scriptFile))) {
            writeScript(writer);
        } catch (IOException | IllegalArgumentException | IllegalAccessException ex) {
            throw new ParserException("Unable to write " + scriptFile, ex);
        }
    }

	private void ensureScriptFileExists(File scriptFile) {
		if (!scriptFile.exists()) {
            try {
				if (!scriptFile.createNewFile()) {
					throw new ParserException("Unable to create the script file.");
				}
			} catch (IOException ex) {
				throw new ParserException("Unable to create the script file.", ex);
			}
        }
	}

    private void writeScript(BufferedWriter writer) throws IOException, IllegalAccessException {
        writer.write("script {");
        writer.newLine();
        writer.write("\tname=");
        writer.append(script.getName());
        writer.newLine();
        writer.write("\tentry=");
        writer.append(script.getEntry());
        writer.newLine();
        if (!script.getComponents().isEmpty()) {
            writer.write("\tcomponents {");
            writer.newLine();
            writeComponents(writer);
            writer.write("\t}");
            writer.newLine();
        }
        writer.write("}");
    }

    private void writeComponents(BufferedWriter writer) throws IOException, IllegalAccessException {
        for (AiComponent component : script.getComponents()) {
            writeComponent(writer, component);
        }
    }

    private void writeComponent(BufferedWriter writer, AiComponent component) throws IllegalAccessException, IOException {
        if (component != null) {
            writer.write("\t\tcomponent {");
            writer.newLine();
            writer.write("\t\t\tclass=");
            writer.write(component.getClass().getName());
            writer.newLine();
            writer.write("\t\t\tid=");
            writer.write(component.getId());
            writer.newLine();
            writeProperties(writer, component);
            writer.newLine();
            writeExits(writer, component);
            writer.write("\t\t}");
            writer.newLine();
        }
    }

    private void writeProperties(BufferedWriter writer, AiComponent component) throws IOException, IllegalAccessException {
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

    private void writeExits(BufferedWriter writer, AiComponent component) throws IOException, IllegalAccessException {
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

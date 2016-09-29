package com.ractoc.fs.parsers.entitytemplate.annotation;

import com.ractoc.fs.parsers.ParserException;
import java.io.*;
import java.util.*;
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.*;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes({"com.ractoc.fs.parsers.entitytemplate.annotation.Template"})
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class TemplateProcessor extends AbstractProcessor {

    private Element componentElement;
    private Template template;
    private Types typeUtils;
    private PropertyType[] properties;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        typeUtils = processingEnv.getTypeUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> elements, RoundEnvironment env) {
        for (Element elem : env.getElementsAnnotatedWith(Template.class)) {
            componentElement = elem;
            processSingleElement();
        }
        return true;
    }

    private void processSingleElement() {
        template = componentElement.getAnnotation(Template.class);
        if (template.generate()) {
            properties = getProperties();
            generateParserForElement();
            generateWriterForElement();
        }
    }

    private void generateParserForElement() {
        try {
            generateTemplateParser();
        } catch (IOException ex) {
            throw new ParserException("Unable to generate Sourcefile", ex);
        }
    }

    private void generateWriterForElement() {
        try {
            generateTemplateWriter();
        } catch (IOException ex) {
            throw new ParserException("Unable to generate Sourcefile", ex);
        }
    }

    private void generateTemplateParser() throws IOException {
        String parserName = template.parser();
        String componentName = getComponentPackageNameAsString() + "." + getComponentClassNameAsString();
        Filer f = processingEnv.getFiler();
        JavaFileObject sourceFile = f.createSourceFile(parserName);
        Writer writer = sourceFile.openWriter();
        ParserGenerator generator = new ParserGenerator(parserName, componentName, properties);
        generator.generate(writer);
        writer.close();
    }

    private void generateTemplateWriter() throws IOException {
        String writerName = template.writer();
        String componentName = getComponentPackageNameAsString() + "." + getComponentClassNameAsString();
        Filer f = processingEnv.getFiler();
        JavaFileObject sourceFile = f.createSourceFile(writerName);
        Writer writer = sourceFile.openWriter();
        WriterGenerator generator = new WriterGenerator(writerName, componentName, properties);
        generator.generate(writer);
        writer.close();
    }

    private PropertyType[] getProperties() {
        ArrayList<PropertyType> propertyTypes = new ArrayList<PropertyType>();
        for (VariableElement variableElement : ElementFilter.fieldsIn(componentElement.getEnclosedElements())) {
            String fieldName = variableElement.getSimpleName().toString();
            Element innerElement = typeUtils.asElement(variableElement.asType());
            String fieldClass = "";
            if (innerElement == null) { // Primitive type
                throw new ParserException("Primitive types are not supported.");
            } else {
                if (innerElement instanceof TypeElement) {
                    TypeElement typeElement = (TypeElement) innerElement;
                    fieldClass = typeElement.getQualifiedName().toString();
                }
            }
            propertyTypes.add(new PropertyType(fieldClass, fieldName));
        }
        return propertyTypes.toArray(new PropertyType[]{});
    }

    private String getComponentPackageNameAsString() {
        Element packageElement = componentElement.getEnclosingElement();
        return packageElement.toString();
    }

    private String getComponentClassNameAsString() {
        Name className = componentElement.getSimpleName();
        return className.toString();
    }
}

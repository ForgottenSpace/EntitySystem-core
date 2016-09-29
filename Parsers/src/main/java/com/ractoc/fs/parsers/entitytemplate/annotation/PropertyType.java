package com.ractoc.fs.parsers.entitytemplate.annotation;

public class PropertyType {
    private final String fieldClass;
    private final String fieldName;

    public PropertyType(String fieldClass, String fieldName) {
        this.fieldClass = fieldClass;
        this.fieldName = fieldName;
    }

    public String getFieldClass() {
        return fieldClass;
    }

    public String getFieldName() {
        return fieldName;
    }
    
}

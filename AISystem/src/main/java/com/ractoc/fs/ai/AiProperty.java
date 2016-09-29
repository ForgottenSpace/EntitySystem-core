package com.ractoc.fs.ai;

public class AiProperty {

    private final String name;
    private final String displayName;
    private final String shortDescription;
    private String value;
    private Class<?> type;

    public AiProperty(String name, String displayName, String shortDescription, String value, Class<?> type) {
        this.name = name;
        this.displayName = displayName;
        this.shortDescription = shortDescription;
        this.value = value;
        this.type = type;
    }

    public Object getValue() {

        return null;
    }
}

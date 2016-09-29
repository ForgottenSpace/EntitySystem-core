package com.ractoc.fs.es;

public abstract class IsEmpty {

    public static boolean componentTypes(Class<? extends EntityComponent>[] componentTypes) {
        boolean result = true;
        if (componentTypes.length > 0) {
            int initializedComponentTypesCount = 0;
            for (Class<? extends EntityComponent> componentType : componentTypes) {
                if (componentType != null) {
                    initializedComponentTypesCount++;
                }
            }
            if (initializedComponentTypesCount == componentTypes.length) {
                result = false;
            }
        }
        return result;
    }

    public static boolean components(EntityComponent[] components) {
        boolean result = true;
        if (components.length > 0) {
        int initializedComponentCount = 0;
            for (EntityComponent component : components) {
                if (component != null) {
                    initializedComponentCount++;
                }
            }
            if (initializedComponentCount == components.length) {
                result = false;
            }
        }
        return result;
    }

}

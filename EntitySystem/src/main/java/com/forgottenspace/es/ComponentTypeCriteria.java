package com.forgottenspace.es;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

public class ComponentTypeCriteria extends ArrayList<Class<? extends EntityComponent>> {

	private static final long serialVersionUID = 5431094934773111297L;

	@SafeVarargs
	public ComponentTypeCriteria(Class<? extends EntityComponent>... componentTypes) {
        if (IsEmpty.componentTypes(componentTypes)) {
            throw new EntityException("Either no component types are supplied or some of the supplied component types are null.");
        } else {
            addAll(Arrays.asList(componentTypes));
        }
    }

	public boolean matches(Set<Class<? extends EntityComponent>> componentTypes) {
		if (componentTypes != null) {
			return componentTypes.containsAll(this);
		}
		return false;
	}
}

package com.forgottenspace.parsers.ai;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AiComponentExit {

    String name();

    @SuppressWarnings("rawtypes")
	Class type();

    String displayName();

    String shortDescription();
}

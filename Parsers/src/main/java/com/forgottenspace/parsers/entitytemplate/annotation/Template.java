package com.forgottenspace.parsers.entitytemplate.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Template {

    String writer();
    String parser();
    boolean generate() default true;
    String model() default "";
    String material() default "";
    String proxyColor() default "";
}

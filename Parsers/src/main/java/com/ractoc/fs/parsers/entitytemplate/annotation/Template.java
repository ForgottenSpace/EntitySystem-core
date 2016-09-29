package com.ractoc.fs.parsers.entitytemplate.annotation;

import com.jme3.math.ColorRGBA;
import java.lang.annotation.*;

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

package com.ost.walletsdk.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.PACKAGE})
public @interface RequiresApi {
    @IntRange(
            from = 1L
    )
    int value() default 1;

    @IntRange(
            from = 1L
    )
    int api() default 1;
}
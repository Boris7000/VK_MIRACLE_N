package com.miracle.engine.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Denotes that a parameter, field or method return value can be empty.
 * <p>
 * When decorating a method call parameter, this denotes that the parameter can
 * legitimately be empty and the method will gracefully deal with it. Typically
 * used on optional parameters.
 * <p>
 * When decorating a method, this denotes the method might legitimately return
 * empty value.
 * <p>
 * This is a marker annotation and it has no specific attributes.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE})
public @interface MayBeEmpty {
}
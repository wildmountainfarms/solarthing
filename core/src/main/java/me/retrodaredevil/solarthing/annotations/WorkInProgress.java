package me.retrodaredevil.solarthing.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to document that something is work in progress and is not complete
 */
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.SOURCE)
public @interface WorkInProgress {
}

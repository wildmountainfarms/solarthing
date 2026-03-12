package me.retrodaredevil.solarthing.annotations;

import org.jspecify.annotations.NullMarked;

import java.lang.annotation.*;

/**
 * Used to document untested methods
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
@NullMarked
public @interface Untested {
}

package me.retrodaredevil.solarthing.annotations;

import java.lang.annotation.*;

/**
 * Used to document untested methods
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface Untested {
}

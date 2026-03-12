package me.retrodaredevil.solarthing.annotations;

import org.jspecify.annotations.NullMarked;

import java.lang.annotation.*;

/**
 * This annotation should go on default methods to stop them from being overridden.
 */
@Target({ElementType.METHOD})
@Inherited
@Retention(RetentionPolicy.SOURCE)
@NullMarked
public @interface DefaultFinal {
}

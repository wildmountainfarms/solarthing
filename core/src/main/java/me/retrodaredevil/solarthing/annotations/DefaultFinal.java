package me.retrodaredevil.solarthing.annotations;

import java.lang.annotation.*;

/**
 * This annotation should go on default methods to stop them from being overridden.
 */
@Target({ElementType.METHOD})
@Inherited
@Retention(RetentionPolicy.SOURCE)
public @interface DefaultFinal {
}

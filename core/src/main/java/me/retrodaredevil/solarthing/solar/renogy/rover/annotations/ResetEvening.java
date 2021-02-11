package me.retrodaredevil.solarthing.solar.renogy.rover.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Documents that a particular field increments or resets in the evening when the charge controller goes to sleep
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface ResetEvening {
}

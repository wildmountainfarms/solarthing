package me.retrodaredevil.solarthing.solar.renogy.rover.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Methods and types annotated with this document that they are only meant to be used with rover devices, not dcdc controllers
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface RoverOnly {
}

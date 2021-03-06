package me.retrodaredevil.solarthing.packets.annotations;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface ValidSinceVersion {
	/**
	 * The packet version this particular method has a value return value since
	 */
	int version();
	boolean probablyValidAnyway() default false;
}

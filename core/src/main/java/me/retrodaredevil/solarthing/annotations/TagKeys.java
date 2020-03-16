package me.retrodaredevil.solarthing.annotations;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TagKeys {
	String[] value();
}

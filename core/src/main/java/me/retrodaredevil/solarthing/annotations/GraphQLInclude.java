package me.retrodaredevil.solarthing.annotations;

import org.jspecify.annotations.NullMarked;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@NullMarked
public @interface GraphQLInclude {
	String value();
}

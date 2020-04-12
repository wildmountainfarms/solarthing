package me.retrodaredevil.solarthing.annotations;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface GraphQLInclude {
	String value();
}

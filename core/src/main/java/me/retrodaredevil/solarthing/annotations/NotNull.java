package me.retrodaredevil.solarthing.annotations;

import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierNickname;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@TypeQualifierNickname
@Nonnull
public @interface NotNull {
	// our goal with this is to be compatible with keep-79: https://github.com/Kotlin/KEEP/issues/79
	// https://kotlinlang.org/docs/java-interop.html#jsr-305-support
}

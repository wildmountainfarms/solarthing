package me.retrodaredevil.solarthing.annotations;

import javax.annotation.CheckForNull;
import javax.annotation.meta.TypeQualifierNickname;
import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;

//@Target({ TYPE, ANNOTATION_TYPE, PARAMETER, TYPE_USE, METHOD})
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@TypeQualifierNickname
@CheckForNull
public @interface Nullable {
}

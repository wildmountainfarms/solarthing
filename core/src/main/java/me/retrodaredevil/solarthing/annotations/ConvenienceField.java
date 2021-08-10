package me.retrodaredevil.solarthing.annotations;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.annotation.*;

/**
 * A convenience field is that which does not need to be included in a packet by default, unless using an {@link com.fasterxml.jackson.databind.ObjectMapper}
 * configured by {@link me.retrodaredevil.solarthing.util.JacksonUtil#includeConvenienceFields(ObjectMapper)}
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Inherited
@JacksonAnnotationsInside
@JsonIgnore // ignore convenience fields by default
@Retention(RetentionPolicy.RUNTIME)
public @interface ConvenienceField {
}

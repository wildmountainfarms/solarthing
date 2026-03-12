package me.retrodaredevil.solarthing.misc.source;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.jspecify.annotations.NullMarked;

@JsonSubTypes({
		@JsonSubTypes.Type(W1Source.class),
})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@NullMarked
public interface DeviceSource {
	String getName();
}

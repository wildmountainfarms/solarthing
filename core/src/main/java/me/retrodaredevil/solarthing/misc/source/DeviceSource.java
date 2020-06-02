package me.retrodaredevil.solarthing.misc.source;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonSubTypes({
		@JsonSubTypes.Type(W1Source.class),
})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public interface DeviceSource {
	String getName();
}

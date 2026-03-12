package me.retrodaredevil.solarthing.type.alter;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

@NullMarked
public interface UniqueRequestIdContainer {

	@JsonProperty("uniqueRequestId")
	UUID getUniqueRequestId();

}

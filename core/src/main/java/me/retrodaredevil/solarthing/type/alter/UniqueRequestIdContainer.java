package me.retrodaredevil.solarthing.type.alter;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

public interface UniqueRequestIdContainer {

	@JsonProperty("uniqueRequestId")
	@NonNull UUID getUniqueRequestId();

}

package me.retrodaredevil.solarthing.alter;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.NotNull;

import java.util.UUID;

public interface UniqueRequestIdContainer {

	@JsonProperty("uniqueRequestId")
	@NotNull UUID getUniqueRequestId();

}

package me.retrodaredevil.solarthing.misc.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.misc.source.DeviceSource;
import org.jspecify.annotations.NonNull;

public interface SourcedData extends DataIdentifiable {
	@JsonProperty("source")
	@NonNull DeviceSource getDeviceSource();
}

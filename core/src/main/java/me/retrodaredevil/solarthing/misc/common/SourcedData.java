package me.retrodaredevil.solarthing.misc.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.misc.source.DeviceSource;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface SourcedData extends DataIdentifiable {
	// TODO remove NonNull
	@JsonProperty("source")
	@NonNull DeviceSource getDeviceSource();
}

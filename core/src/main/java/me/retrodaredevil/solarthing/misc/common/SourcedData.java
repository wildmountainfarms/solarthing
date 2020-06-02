package me.retrodaredevil.solarthing.misc.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.misc.source.DeviceSource;

public interface SourcedData extends DataIdentifiable {
	@JsonProperty("source")
	DeviceSource getDeviceSource();
}

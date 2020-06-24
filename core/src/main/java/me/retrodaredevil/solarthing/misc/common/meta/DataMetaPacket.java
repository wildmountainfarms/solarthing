package me.retrodaredevil.solarthing.misc.common.meta;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.meta.TargetedMetaPacket;
import me.retrodaredevil.solarthing.misc.common.DataIdentifiable;

public interface DataMetaPacket extends TargetedMetaPacket, DataIdentifiable {
	@JsonProperty("name")
	@NotNull String getName();
	@JsonProperty("description")
	@NotNull String getDescription();
	@JsonProperty("location")
	@NotNull String getLocation();
}

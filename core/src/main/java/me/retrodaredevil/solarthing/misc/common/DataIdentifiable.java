package me.retrodaredevil.solarthing.misc.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;

public interface DataIdentifiable extends Identifiable {
	@Override
	@NotNull DataIdentifier getIdentifier();

	@JsonProperty("dataId")
	int getDataId();
}

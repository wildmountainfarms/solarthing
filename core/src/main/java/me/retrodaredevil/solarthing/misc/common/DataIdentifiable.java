package me.retrodaredevil.solarthing.misc.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;
import org.jspecify.annotations.NonNull;

public interface DataIdentifiable extends Identifiable {
	@Override
	@NonNull DataIdentifier getIdentifier();

	@JsonProperty("dataId")
	int getDataId();
}

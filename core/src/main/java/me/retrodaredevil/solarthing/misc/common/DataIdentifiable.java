package me.retrodaredevil.solarthing.misc.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface DataIdentifiable extends Identifiable {
	// TODO remove NonNull
	@Override
	@NonNull DataIdentifier getIdentifier();

	@JsonProperty("dataId")
	int getDataId();
}

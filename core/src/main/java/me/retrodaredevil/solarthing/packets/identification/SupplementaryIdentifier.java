package me.retrodaredevil.solarthing.packets.identification;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface SupplementaryIdentifier extends Identifier {
	// TODO remove NonNull
	/**
	 * This is not null
	 * @return The {@link Identifier} that this is supplementary to
	 */
	@JsonProperty("supplementaryTo")
	@NonNull Identifier getSupplementaryTo();
}

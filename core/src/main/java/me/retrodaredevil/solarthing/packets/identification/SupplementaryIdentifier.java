package me.retrodaredevil.solarthing.packets.identification;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public interface SupplementaryIdentifier extends Identifier {
	/**
	 * This is not null
	 * @return The {@link Identifier} that this is supplementary to
	 */
	@JsonProperty("supplementaryTo")
	@NotNull Identifier getSupplementaryTo();
}

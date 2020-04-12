package me.retrodaredevil.solarthing.packets.identification;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface Identifier {
	/**
	 * @return A representation of this identifier
	 */
	@JsonProperty("representation")
	String getRepresentation();
}

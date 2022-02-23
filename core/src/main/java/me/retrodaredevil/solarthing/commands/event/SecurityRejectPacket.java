package me.retrodaredevil.solarthing.commands.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.WorkInProgress;
import me.retrodaredevil.solarthing.marker.EventPacket;

@WorkInProgress
public interface SecurityRejectPacket extends EventPacket {

	@JsonProperty("reason")
	@NotNull Reason getReason();

	@JsonProperty("moreInfo")
	@NotNull String getMoreInfo();

	enum Reason {
		/** Used for {@link me.retrodaredevil.solarthing.packets.security.SecurityPacketType#LARGE_INTEGRITY_PACKET} to indicate
		 * the payload's hash did not match the encrypted hash*/
		DIFFERENT_PAYLOAD,
		INVALID_DATA,
		PARSE_ERROR,
		DECRYPT_ERROR,
		UNAUTHORIZED,
		CLOCK_VARIANCE,
		UNKNOWN_ERROR,
		LEGACY_REQUEST,
	}
}

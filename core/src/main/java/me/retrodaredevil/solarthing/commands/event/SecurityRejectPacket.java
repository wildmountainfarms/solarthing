package me.retrodaredevil.solarthing.commands.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;

@JsonDeserialize(as = ImmutableSecurityRejectPacket.class)
@JsonTypeName("REJECT")
@JsonExplicit
public interface SecurityRejectPacket extends SecurityEventPacket {

	@Override
	default @NotNull SecurityEventPacketType getPacketType() {
		return SecurityEventPacketType.REJECT;
	}

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

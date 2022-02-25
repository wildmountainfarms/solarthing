package me.retrodaredevil.solarthing.commands.event;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;

@JsonDeserialize(as = ImmutableSecurityAcceptPacket.class)
@JsonTypeName("ACCEPT")
@JsonExplicit
public interface SecurityAcceptPacket extends SecurityEventPacket {

	@Override
	default @NotNull SecurityEventPacketType getPacketType() {
		return SecurityEventPacketType.ACCEPT;
	}
}

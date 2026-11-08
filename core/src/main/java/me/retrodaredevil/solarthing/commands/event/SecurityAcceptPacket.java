package me.retrodaredevil.solarthing.commands.event;

import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import org.jspecify.annotations.NullMarked;
import tools.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = ImmutableSecurityAcceptPacket.class)
@JsonTypeName("ACCEPT")
@JsonExplicit
@NullMarked
public interface SecurityAcceptPacket extends SecurityEventPacket {

	@Override
	default SecurityEventPacketType getPacketType() {
		return SecurityEventPacketType.ACCEPT;
	}
}

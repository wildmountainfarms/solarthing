package me.retrodaredevil.solarthing.commands.event;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import org.jspecify.annotations.NullMarked;

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

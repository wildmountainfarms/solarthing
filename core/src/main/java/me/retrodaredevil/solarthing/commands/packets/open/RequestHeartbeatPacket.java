package me.retrodaredevil.solarthing.commands.packets.open;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.type.alter.UniqueRequestIdContainer;
import me.retrodaredevil.solarthing.type.event.feedback.HeartbeatData;
import org.jspecify.annotations.NullMarked;

@JsonDeserialize(as = ImmutableRequestHeartbeatPacket.class)
@JsonTypeName("REQUEST_HEARTBEAT")
@JsonExplicit
@NullMarked
public interface RequestHeartbeatPacket extends CommandOpenPacket, UniqueRequestIdContainer {
	@Override
	default CommandOpenPacketType getPacketType() {
		return CommandOpenPacketType.REQUEST_HEARTBEAT;
	}

	@JsonProperty("data")
	HeartbeatData getData();

}

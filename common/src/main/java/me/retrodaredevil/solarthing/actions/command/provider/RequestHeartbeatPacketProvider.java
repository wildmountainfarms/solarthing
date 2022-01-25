package me.retrodaredevil.solarthing.actions.command.provider;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.commands.packets.open.CommandOpenPacket;
import me.retrodaredevil.solarthing.commands.packets.open.ImmutableRequestHeartbeatPacket;
import me.retrodaredevil.solarthing.type.event.feedback.HeartbeatData;

import java.util.UUID;

import static java.util.Objects.requireNonNull;

@JsonTypeName("requestheartbeat")
public class RequestHeartbeatPacketProvider implements CommandOpenProvider {
	private final HeartbeatData heartbeatData;

	@JsonCreator
	public RequestHeartbeatPacketProvider(@JsonProperty(value = "data", required = true) HeartbeatData heartbeatData) {
		requireNonNull(this.heartbeatData = heartbeatData);
	}

	@Override
	public @NotNull CommandOpenPacket get() {
		return new ImmutableRequestHeartbeatPacket(heartbeatData, UUID.randomUUID());
	}
}

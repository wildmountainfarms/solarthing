package me.retrodaredevil.solarthing.actions.command.provider;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.commands.packets.open.CommandOpenPacket;
import me.retrodaredevil.solarthing.commands.packets.open.ImmutableRequestHeartbeatPacket;
import me.retrodaredevil.solarthing.type.event.feedback.HeartbeatData;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

import static java.util.Objects.requireNonNull;

@JsonTypeName("requestheartbeat")
@NullMarked
public class RequestHeartbeatPacketProvider implements CommandOpenProvider {
	private final HeartbeatData heartbeatData;

	@JsonCreator
	public RequestHeartbeatPacketProvider(@JsonProperty(value = "heartbeat", required = true) @Nullable HeartbeatData heartbeatData) {
		this.heartbeatData = requireNonNull(heartbeatData);
	}

	@Override
	public CommandOpenPacket get() {
		return new ImmutableRequestHeartbeatPacket(heartbeatData, UUID.randomUUID());
	}
}

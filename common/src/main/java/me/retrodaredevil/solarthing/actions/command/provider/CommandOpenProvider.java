package me.retrodaredevil.solarthing.actions.command.provider;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import me.retrodaredevil.solarthing.commands.packets.open.CommandOpenPacket;
import org.jspecify.annotations.NullMarked;

/**
 * A simple interface to provide {@link CommandOpenPacket}s.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(RequestCommandPacketProvider.class),
		@JsonSubTypes.Type(RequestHeartbeatPacketProvider.class),
})
@NullMarked
public interface CommandOpenProvider {
	CommandOpenPacket get();
}

package me.retrodaredevil.solarthing.commands.packets.status;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.commands.CommandInfo;

import java.util.List;

public class ImmutableAvailableCommandsPacket implements AvailableCommandsPacket {
	private final List<CommandInfo> commandInfoList;

	@JsonCreator
	public ImmutableAvailableCommandsPacket(@JsonProperty("commands") List<CommandInfo> commandInfoList) {
		this.commandInfoList = commandInfoList;
	}

	@Override
	public List<CommandInfo> getCommandInfoList() {
		return commandInfoList;
	}
}

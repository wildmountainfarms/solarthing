package me.retrodaredevil.solarthing.solar.outback.command.packets;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.solar.outback.command.MateCommand;

import static java.util.Objects.requireNonNull;

public class ImmutableSuccessMateCommandPacket implements SuccessMateCommandPacket {
	
	private final MateCommand command;
	private final String source;

	@JsonCreator
	public ImmutableSuccessMateCommandPacket(
			@JsonProperty(value = "command", required = true) MateCommand command,
			@JsonProperty(value = "source", required = true) String source
	) {
		this.command = requireNonNull(command);
		this.source = requireNonNull(source);
	}
	
	@Override
	public MateCommand getCommand() {
		return command;
	}
	
	@Override
	public String getSource() {
		return source;
	}
	
}

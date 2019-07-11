package me.retrodaredevil.solarthing.solar.outback.command.packets;

import me.retrodaredevil.solarthing.solar.outback.command.MateCommand;

import static java.util.Objects.requireNonNull;

public class ImmutableSuccessCommandPacket implements SuccessCommandPacket {
	
	private final CommandFeedbackPacketType packetType = CommandFeedbackPacketType.SUCCESS;
	
	private final MateCommand command;
	private final String source;
	
	public ImmutableSuccessCommandPacket(MateCommand command, String source) {
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
	
	@Override
	public CommandFeedbackPacketType getPacketType() {
		return packetType;
	}
}

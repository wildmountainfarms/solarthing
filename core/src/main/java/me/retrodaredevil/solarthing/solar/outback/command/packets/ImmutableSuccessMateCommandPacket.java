package me.retrodaredevil.solarthing.solar.outback.command.packets;

import me.retrodaredevil.solarthing.solar.outback.command.MateCommand;

import static java.util.Objects.requireNonNull;

public class ImmutableSuccessMateCommandPacket implements SuccessMateCommandPacket {
	
	private final MateCommandFeedbackPacketType packetType = MateCommandFeedbackPacketType.SUCCESS;
	
	private final MateCommand command;
	private final String source;
	
	public ImmutableSuccessMateCommandPacket(MateCommand command, String source) {
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
	public MateCommandFeedbackPacketType getPacketType() {
		return packetType;
	}
}

package me.retrodaredevil.solarthing.program;

import me.retrodaredevil.solarthing.InstantType;
import me.retrodaredevil.solarthing.commands.OnCommandExecute;
import me.retrodaredevil.solarthing.commands.SourcedCommand;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;
import me.retrodaredevil.solarthing.solar.outback.command.MateCommand;
import me.retrodaredevil.solarthing.solar.outback.command.packets.ImmutableSuccessMateCommandPacket;

import java.util.Collections;

public class OnMateCommandSent implements OnCommandExecute<MateCommand> {
//	private static final Logger LOGGER = LoggerFactory.getLogger(OnMateCommandSent.class);

	private final PacketListReceiver packetListReceiver;

	public OnMateCommandSent(PacketListReceiver packetListReceiver) {
		this.packetListReceiver = packetListReceiver;
	}

	@Override
	public void onCommandExecute(SourcedCommand<MateCommand> command) {
		Packet packet = new ImmutableSuccessMateCommandPacket(command.getCommand(), command.getSource());
		packetListReceiver.receive(Collections.singletonList(packet), InstantType.INSTANT);
	}
}

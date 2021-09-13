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
		// TODO This is the main point in our code where our use of OpenSource stops and we have to convert it to a DataSource then a string
		//   We might consider a way to maintain legacy compatibility and start using OpenSource in SuccessMateCommandPackets
		Packet packet = new ImmutableSuccessMateCommandPacket(command.getCommand(), command.getSource().toDataSource().toString());
		packetListReceiver.receive(Collections.singletonList(packet), InstantType.INSTANT);
	}
}

package me.retrodaredevil.solarthing.program;

import me.retrodaredevil.solarthing.commands.command.OnCommandExecute;
import me.retrodaredevil.solarthing.commands.command.SourcedCommand;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;
import me.retrodaredevil.solarthing.reason.ExecutionReason;
import me.retrodaredevil.solarthing.reason.OpenSourceExecutionReason;
import me.retrodaredevil.solarthing.solar.outback.command.MateCommand;
import me.retrodaredevil.solarthing.solar.outback.command.packets.ImmutableSuccessMateCommandPacket;
import me.retrodaredevil.solarthing.solar.outback.command.packets.SuccessMateCommandPacket;

import java.util.Collections;

public class OnMateCommandSent implements OnCommandExecute<MateCommand> {
//	private static final Logger LOGGER = LoggerFactory.getLogger(OnMateCommandSent.class);

	private final PacketListReceiver packetListReceiver;

	public OnMateCommandSent(PacketListReceiver packetListReceiver) {
		this.packetListReceiver = packetListReceiver;
	}

	@Override
	public void onCommandExecute(SourcedCommand<MateCommand> command) {
		ExecutionReason executionReason = command.getExecutionReason();
		final String dataSource;
		if (executionReason instanceof OpenSourceExecutionReason) {
			dataSource = ((OpenSourceExecutionReason) executionReason).getSource().toDataSource().toString(); // for legacy reasons, include the data source converted to a string
		} else {
			dataSource = executionReason.getUniqueString(); // this may occur for new packets
		}
		Packet packet = new ImmutableSuccessMateCommandPacket(
				SuccessMateCommandPacket.VERSION_LATEST,
				command.getCommand(),
				dataSource,
				executionReason
		);
		packetListReceiver.receive(Collections.singletonList(packet));
	}
}

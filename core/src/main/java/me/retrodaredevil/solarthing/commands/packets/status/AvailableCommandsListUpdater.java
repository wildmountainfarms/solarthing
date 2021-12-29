package me.retrodaredevil.solarthing.commands.packets.status;

import me.retrodaredevil.solarthing.commands.CommandInfo;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;

import java.util.List;

public class AvailableCommandsListUpdater implements PacketListReceiver {
	private final List<CommandInfo> commandInfoList;
	private final boolean alwaysSend;

	public AvailableCommandsListUpdater(List<CommandInfo> commandInfoList, boolean alwaysSend) {
		this.commandInfoList = commandInfoList;
		this.alwaysSend = alwaysSend;
	}

	@Override
	public void receive(List<Packet> packets) {
		if (alwaysSend || !packets.isEmpty()) {
			packets.add(new ImmutableAvailableCommandsPacket(commandInfoList));
		}
	}
}

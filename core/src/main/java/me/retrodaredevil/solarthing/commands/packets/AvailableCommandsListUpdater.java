package me.retrodaredevil.solarthing.commands.packets;

import me.retrodaredevil.solarthing.InstantType;
import me.retrodaredevil.solarthing.commands.CommandInfo;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;

import java.util.List;

public class AvailableCommandsListUpdater implements PacketListReceiver {
	private final List<CommandInfo> commandInfoList;

	public AvailableCommandsListUpdater(List<CommandInfo> commandInfoList) {
		this.commandInfoList = commandInfoList;
	}

	@Override
	public void receive(List<Packet> packets, InstantType instantType) {
		packets.add(new ImmutableAvailableCommandsPacket(commandInfoList));
	}
}

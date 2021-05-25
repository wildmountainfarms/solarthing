package me.retrodaredevil.solarthing.program;

import me.retrodaredevil.solarthing.InstantType;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;
import me.retrodaredevil.solarthing.solar.tracer.TracerReadTable;

import java.util.List;

public class TracerPacketListUpdater implements PacketListReceiver {
	private final TracerReadTable read;

	public TracerPacketListUpdater(TracerReadTable read) {
		this.read = read;
	}

	@Override
	public void receive(List<Packet> packets, InstantType instantType) {

	}
}

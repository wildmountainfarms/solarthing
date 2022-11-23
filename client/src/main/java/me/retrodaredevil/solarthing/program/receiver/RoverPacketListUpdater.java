package me.retrodaredevil.solarthing.program.receiver;

import me.retrodaredevil.solarthing.netcat.ConnectionHandler;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;
import me.retrodaredevil.solarthing.program.receiver.util.NetCatUtil;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverReadTable;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverStatusPacket;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverStatusPackets;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverWriteTable;

import java.util.List;

import static java.util.Objects.requireNonNull;

public class RoverPacketListUpdater implements PacketListReceiver {

	private final int number;
	private final RoverReadTable read;
	private final RoverWriteTable write;
	private final ConnectionHandler connectionHandler;

	public RoverPacketListUpdater(int number, RoverReadTable read, RoverWriteTable write, ConnectionHandler connectionHandler) {
		this.number = number;
		requireNonNull(this.read = read);
		requireNonNull(this.write = write);
		this.connectionHandler = connectionHandler;
	}

	@Override
	public void receive(List<Packet> packets) {
		RoverStatusPacket packet = RoverStatusPackets.createFromReadTable(number, read);
		packets.add(packet);
		if (connectionHandler != null) {
			connectionHandler.handleRequests(request -> NetCatUtil.handle(write, packet, request));
		}
	}
}

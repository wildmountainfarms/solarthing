package me.retrodaredevil.solarthing.program.receiver;

import me.retrodaredevil.solarthing.InstantType;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.netcat.ConnectionHandler;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;
import me.retrodaredevil.solarthing.program.receiver.util.NetCatUtil;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverReadTable;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverStatusPacket;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverStatusPackets;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverWriteTable;
import me.retrodaredevil.solarthing.solar.renogy.rover.special.SpecialPowerControl_E02D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static java.util.Objects.requireNonNull;

public class RoverPacketListUpdater implements PacketListReceiver {
	private static final Logger LOGGER = LoggerFactory.getLogger(RoverPacketListUpdater.class);

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
	public void receive(List<Packet> packets, InstantType instantType) {
		RoverStatusPacket packet = RoverStatusPackets.createFromReadTable(number, read);
		SpecialPowerControl_E02D specialPower2 = packet.getSpecialPowerControlE02D();
		LOGGER.debug(SolarThingConstants.NO_CONSOLE, "Debugging special power control values: (Will debug all packets later)\n" +
				packet.getSpecialPowerControlE021().getFormattedInfo().replaceAll("\n", "\n\t") +
				(specialPower2 == null ? "" : "\n" + specialPower2.getFormattedInfo().replaceAll("\n", "\n\t"))
		);
		packets.add(packet);
		if (connectionHandler != null) {
			connectionHandler.handleRequests(request -> NetCatUtil.handle(write, packet, request));
		}
	}
}

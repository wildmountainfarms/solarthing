package me.retrodaredevil.solarthing.program;

import me.retrodaredevil.solarthing.InstantType;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;
import me.retrodaredevil.solarthing.solar.tracer.TracerReadTable;
import me.retrodaredevil.solarthing.solar.tracer.TracerStatusPacket;
import me.retrodaredevil.solarthing.solar.tracer.TracerStatusPackets;
import me.retrodaredevil.solarthing.solar.tracer.TracerWriteTable;

import java.util.List;

public class TracerPacketListUpdater implements PacketListReceiver {
//	private static final Logger LOGGER = LoggerFactory.getLogger(TracerPacketListUpdater.class);
//	private static final ObjectMapper MAPPER = JacksonUtil.defaultMapper();

	private final int number;
	private final TracerReadTable read;
	private final TracerWriteTable write;

	public TracerPacketListUpdater(int number, TracerReadTable read, TracerWriteTable write) {
		this.number = number;
		this.read = read;
		this.write = write;
	}

	@Override
	public void receive(List<Packet> packets, InstantType instantType) {
		TracerStatusPacket packet = TracerStatusPackets.createFromReadTable(number, read);
		packets.add(packet);
	}
}

package me.retrodaredevil.solarthing.program;

import me.retrodaredevil.solarthing.InstantType;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.config.request.TracerClockOptions;
import me.retrodaredevil.solarthing.netcat.ConnectionHandler;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;
import me.retrodaredevil.solarthing.solar.tracer.TracerReadTable;
import me.retrodaredevil.solarthing.solar.tracer.TracerStatusPacket;
import me.retrodaredevil.solarthing.solar.tracer.TracerStatusPackets;
import me.retrodaredevil.solarthing.solar.tracer.TracerWriteTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.*;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class TracerPacketListUpdater implements PacketListReceiver {
	private static final Logger LOGGER = LoggerFactory.getLogger(TracerPacketListUpdater.class);
//	private static final ObjectMapper MAPPER = JacksonUtil.defaultMapper();

	private final int number;
	private final TracerReadTable read;
	private final TracerWriteTable write;
	private final TracerClockOptions tracerClockOptions;
	private final ConnectionHandler connectionHandler;

	public TracerPacketListUpdater(int number, TracerReadTable read, TracerWriteTable write, TracerClockOptions tracerClockOptions, ConnectionHandler connectionHandler) {
		this.number = number;
		requireNonNull(this.read = read);
		requireNonNull(this.write = write);
		this.tracerClockOptions = tracerClockOptions;
		this.connectionHandler = connectionHandler;
	}

	@Override
	public void receive(List<Packet> packets, InstantType instantType) {
		TracerStatusPacket packet = TracerStatusPackets.createFromReadTable(number, read);
		packets.add(packet);
		if (tracerClockOptions != null) {
			// Sorry to anyone in the year 2256, that's just too bad for you
			// We make the year 21 represent 2021, because the tracer stores the year using 8 bits
			LocalDateTime currentTime = packet.getSolarThingLocalDateTime();
			final LocalDateTime desiredTime;
			ZoneOffset zoneOffset = tracerClockOptions.getZoneOffset();
			if (zoneOffset != null) {
				desiredTime = Instant.now().atOffset(zoneOffset).toLocalDateTime();
			} else {
				ZoneId zone = tracerClockOptions.getZoneId() != null ? tracerClockOptions.getZoneId() : ZoneId.systemDefault();
				desiredTime = Instant.now().atZone(zone).toLocalDateTime();
			}
			if (Duration.between(currentTime, desiredTime).abs().compareTo(tracerClockOptions.getDurationThreshold()) > 0) {
				LOGGER.info("Going to update time to " + desiredTime + " from " + currentTime);
				write.setSolarThingLocalDateTime(desiredTime);
				LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Success updating time!");
			}
		}
		if (connectionHandler != null) {
			connectionHandler.handleRequests(request -> NetCatUtil.handle(write, packet, request));
		}
	}
}

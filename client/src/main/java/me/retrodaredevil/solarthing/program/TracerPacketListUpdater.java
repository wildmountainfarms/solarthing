package me.retrodaredevil.solarthing.program;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.solarthing.InstantType;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;
import me.retrodaredevil.solarthing.solar.tracer.TracerReadTable;
import me.retrodaredevil.solarthing.solar.tracer.TracerWriteTable;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TracerPacketListUpdater implements PacketListReceiver {
	private static final Logger LOGGER = LoggerFactory.getLogger(TracerPacketListUpdater.class);
	private static final ObjectMapper MAPPER = JacksonUtil.defaultMapper();

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
		// We aren't actually going to save a status packet yet.
		// I likely still need to test this before I finalize the tracer status packet to go into a live database
		LOGGER.debug("Here's the tracer:");
		try {
			LOGGER.debug(MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(read));
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}

	}
}

package me.retrodaredevil.solarthing.program.receiver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import me.retrodaredevil.io.modbus.ModbusRuntimeException;
import me.retrodaredevil.io.modbus.handling.FunctionCodeException;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.config.request.TracerClockOptions;
import me.retrodaredevil.solarthing.netcat.ConnectionHandler;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;
import me.retrodaredevil.solarthing.program.receiver.util.NetCatUtil;
import me.retrodaredevil.solarthing.solar.tracer.TracerReadTable;
import me.retrodaredevil.solarthing.solar.tracer.TracerStatusPacket;
import me.retrodaredevil.solarthing.solar.tracer.TracerStatusPackets;
import me.retrodaredevil.solarthing.solar.tracer.TracerWriteTable;
import me.retrodaredevil.solarthing.solar.tracer.batteryconfig.TracerBatteryConfigBuilder;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import me.retrodaredevil.solarthing.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.*;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class TracerPacketListUpdater implements PacketListReceiver {
	private static final Logger LOGGER = LoggerFactory.getLogger(TracerPacketListUpdater.class);
	private static final ObjectMapper MAPPER = JacksonUtil.defaultMapper();

	private final int number;
	private final TracerReadTable read;
	private final TracerWriteTable write;
	private final TracerClockOptions tracerClockOptions;
	private final ConnectionHandler connectionHandler;
	/** Set to true if we queue up {@link TracerBatteryConfigBuilder} fields before writing them */
	private final boolean connectionHandlerHasFlushLogic;

	public TracerPacketListUpdater(int number, TracerReadTable read, TracerWriteTable write, TracerClockOptions tracerClockOptions, ConnectionHandler connectionHandler, boolean connectionHandlerHasFlushLogic) {
		this.number = number;
		requireNonNull(this.read = read);
		requireNonNull(this.write = write);
		this.tracerClockOptions = tracerClockOptions;
		this.connectionHandler = connectionHandler;
		this.connectionHandlerHasFlushLogic = connectionHandlerHasFlushLogic;
	}

	@Override
	public void receive(List<Packet> packets) {
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
				// Logging both as summary is important because we want to be able to see the time in the summary logs
				LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Going to update time to " + desiredTime + " from " + currentTime);
				write.setSolarThingLocalDateTime(desiredTime);
				LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Success updating time!");
			}
		}
		if (connectionHandler != null) {
			TracerBatteryConfigBuilder builder = new TracerBatteryConfigBuilder(packet);
			connectionHandler.handleRequests(request -> {
				String[] split = StringUtil.terminalSplit(request);
				if (split.length == 1) {
					if ("flushBatteryConfig".equals(split[0])) {
						try {
							write.setBatteryConfig(builder);
						} catch (FunctionCodeException ex) {
							return ex.getMessage();
						} catch (ModbusRuntimeException ex) {
							LOGGER.error("Got modbus exception while flushing", ex);
							return "Unknown error:" + ex.getMessage();
						}
						return "flush success";
					}
				}
				if (connectionHandlerHasFlushLogic && split.length > 1) { // check if we're writing something
					String fieldName = split[0];
					String value = split[1];

					// If we can successfully read from this reader, then that means that the input is a field in TracerBatteryConfigBuilder
					//
					ObjectReader reader = MAPPER.readerForUpdating(builder);
					ObjectNode input = new ObjectNode(JsonNodeFactory.instance);
					input.put(fieldName, value);
					boolean success = true;
					try {
						reader.readValue(input);
					} catch (IOException e) {
						success = false;
					}
					if (success) {
						return "queued up";
					}
				}
				return NetCatUtil.handle(write, packet, request);
			});
		}
	}
}

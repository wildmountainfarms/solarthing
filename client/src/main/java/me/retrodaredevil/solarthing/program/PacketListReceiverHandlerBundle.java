package me.retrodaredevil.solarthing.program;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.config.options.CommandOption;
import me.retrodaredevil.solarthing.config.options.PacketHandlingOption;
import me.retrodaredevil.solarthing.packets.collection.PacketCollectionIdGenerator;
import me.retrodaredevil.solarthing.packets.handling.PacketHandler;
import me.retrodaredevil.solarthing.packets.handling.PacketHandlerMultiplexer;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiverMultiplexer;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

public final class PacketListReceiverHandlerBundle {
	private static final Logger LOGGER = LoggerFactory.getLogger(PacketListReceiverHandlerBundle.class);
	private static final ObjectMapper MAPPER = JacksonUtil.defaultMapper();

	private final PacketListReceiverHandler statusHandler;
	private final PacketListReceiverHandler eventHandler;

	public PacketListReceiverHandlerBundle(PacketListReceiverHandler statusHandler, PacketListReceiverHandler eventHandler) {
		this.statusHandler = statusHandler;
		this.eventHandler = eventHandler;
	}


	public PacketListReceiverHandler getStatusHandler() {
		return statusHandler;
	}

	public PacketListReceiverHandler getEventHandler() {
		return eventHandler;
	}
	public List<PacketListReceiver> createDefaultPacketListReceivers() {
		return Arrays.asList(
				getStatusHandler().getPacketListReceiverAccepter(), // store packets present in list to prepare them to be made into a PacketCollection
				getStatusHandler().getPacketListReceiverPacker(), // pack stored status packets into a PacketCollection
				getEventHandler().getPacketListReceiverPacker(), // pack stored event packets into a PacketCollection
				getStatusHandler().getPacketListReceiverHandler(), // send the status PacketCollection off to the database
				getEventHandler().getPacketListReceiverHandler() // send the event PacketCollection off to the database
		);
	}
	public static <T extends PacketHandlingOption & CommandOption> PacketListReceiverHandler createEventPacketListReceiverHandler(PacketListReceiver sourceAndFragmentUpdater, ZoneId zoneId, PacketHandlerBundle packetHandlerBundle) {
		PacketHandler eventPacketHandler = new PacketHandlerMultiplexer(packetHandlerBundle.getEventPacketHandlers());
		return new PacketListReceiverHandler(
				new PacketListReceiverMultiplexer(
						sourceAndFragmentUpdater,
						(packets) -> {
							LOGGER.debug(SolarThingConstants.NO_CONSOLE, "Debugging event packets");
							try {
								LOGGER.debug(SolarThingConstants.NO_CONSOLE, MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(packets));
							} catch (JsonProcessingException e) {
								LOGGER.debug("Never mind about that...", e);
							}
						}
				),
				eventPacketHandler,
				PacketCollectionIdGenerator.Defaults.UNIQUE_GENERATOR,
				zoneId
		);
	}

	public static <T extends PacketHandlingOption & CommandOption> PacketListReceiverHandlerBundle createFrom(T options, PacketHandlerBundle packetHandlerBundle, List<PacketHandler> statusPacketHandlers) {
		PacketListReceiver sourceAndFragmentUpdater = SolarMain.getSourceAndFragmentUpdater(options);
		PacketListReceiverHandler eventPacketListReceiverHandler = createEventPacketListReceiverHandler(sourceAndFragmentUpdater, options.getZoneId(), packetHandlerBundle);
		PacketListReceiverHandler statusPacketListReceiverHandler = new PacketListReceiverHandler(
				new PacketListReceiverMultiplexer(
						sourceAndFragmentUpdater,
						(packets) -> {
							LOGGER.debug("Debugging all packets");
							try {
								LOGGER.debug(MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(packets));
							} catch (JsonProcessingException e) {
								LOGGER.debug("Never mind about that...", e);
							}
						}
				),
				new PacketHandlerMultiplexer(statusPacketHandlers),
				SolarMain.createIdGenerator(options.getUniqueIdsInOneHour(), options.isDocumentIdShort()),
				options.getZoneId()
		);
		return new PacketListReceiverHandlerBundle(statusPacketListReceiverHandler, eventPacketListReceiverHandler);
	}
}

package me.retrodaredevil.solarthing.program;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import me.retrodaredevil.solarthing.config.options.MessageSenderProgramOptions;
import me.retrodaredevil.solarthing.couchdb.CouchDbQueryHandler;
import me.retrodaredevil.solarthing.couchdb.SolarThingCouchDb;
import me.retrodaredevil.solarthing.message.MessageSender;
import me.retrodaredevil.solarthing.message.MessageSenderMultiplexer;
import me.retrodaredevil.solarthing.misc.device.DevicePacket;
import me.retrodaredevil.solarthing.misc.error.ErrorPacket;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.packets.collection.PacketGroup;
import me.retrodaredevil.solarthing.packets.collection.parsing.ObjectMapperPacketConverter;
import me.retrodaredevil.solarthing.packets.collection.parsing.PacketGroupParser;
import me.retrodaredevil.solarthing.packets.collection.parsing.PacketParserMultiplexer;
import me.retrodaredevil.solarthing.packets.collection.parsing.SimplePacketGroupParser;
import me.retrodaredevil.solarthing.packets.handling.PacketHandleException;
import me.retrodaredevil.solarthing.packets.instance.InstancePacket;
import me.retrodaredevil.solarthing.solar.SolarStatusPacket;
import me.retrodaredevil.solarthing.solar.common.BatteryVoltage;
import me.retrodaredevil.solarthing.solar.extra.SolarExtraPacket;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageSenderMain {
	private MessageSenderMain() { throw new UnsupportedOperationException(); }

	private static final Logger LOGGER = LoggerFactory.getLogger(MessageSenderMain.class);
	private static final ObjectMapper CONFIG_MAPPER = JacksonUtil.defaultMapper();
	private static final ObjectMapper PARSE_MAPPER = JacksonUtil.lenientMapper(JacksonUtil.defaultMapper());

	private static List<MessageSender> getMessageSenders(MessageSenderProgramOptions options) throws IOException {
		List<MessageSender> senders = new ArrayList<>();
		for (File file : options.getMessageSenderFiles()) {
			MessageSender sender = CONFIG_MAPPER.readValue(file, MessageSender.class);
			senders.add(sender);
		}
		return senders;
	}

	public static int startMessageSender(MessageSenderProgramOptions options) {
		CouchDbQueryHandler queryHandler = SolarMain.createCouchDbQueryHandler(options);
		PacketGroupParser statusParser = new SimplePacketGroupParser(new PacketParserMultiplexer(Arrays.asList(
				new ObjectMapperPacketConverter(PARSE_MAPPER, SolarStatusPacket.class),
				new ObjectMapperPacketConverter(PARSE_MAPPER, SolarExtraPacket.class),
				new ObjectMapperPacketConverter(PARSE_MAPPER, DevicePacket.class),
				new ObjectMapperPacketConverter(PARSE_MAPPER, ErrorPacket.class),
				new ObjectMapperPacketConverter(PARSE_MAPPER, InstancePacket.class)
		), PacketParserMultiplexer.LenientType.FAIL_WHEN_UNHANDLED_WITH_EXCEPTION));
		final List<MessageSender> senders;
		try {
			senders = new ArrayList<>(getMessageSenders(options));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		senders.add(message -> LOGGER.debug("Sending message: {}", message));
		MessageSender sender = new MessageSenderMultiplexer(senders);

		while (!Thread.currentThread().isInterrupted()) {
			List<ObjectNode> statusPacketNodes = null;
			try {
				long now = System.currentTimeMillis();
				statusPacketNodes = queryHandler.query(SolarThingCouchDb.createMillisView()
						.startKey(now - 5 * 60 * 1000)
						.endKey(now));
				LOGGER.debug("Got packets");
			} catch (PacketHandleException e) {
				LOGGER.error("Couldn't get status packets", e);
			}
			if(statusPacketNodes != null) {
				List<FragmentedPacketGroup> packetGroups = PacketUtil.getPacketGroups(options.getSourceId(), options.getDefaultInstanceOptions(), statusPacketNodes, statusParser);
				if (packetGroups != null) {
					FragmentedPacketGroup packetGroup = packetGroups.get(packetGroups.size() - 1);
					for (Packet packet : packetGroup.getPackets()) {
						if (packet instanceof BatteryVoltage) {
							sender.sendMessage("@retrodaredevil Battery Voltage: " + ((BatteryVoltage) packet).getBatteryVoltage());
							break;
						}
					}
				}
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
				throw new RuntimeException(ex);
			}
		}
		return 0;
	}
}

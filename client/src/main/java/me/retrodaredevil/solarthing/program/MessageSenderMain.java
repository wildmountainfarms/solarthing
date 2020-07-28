package me.retrodaredevil.solarthing.program;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.actions.ActionNode;
import me.retrodaredevil.solarthing.actions.environment.ActionEnvironment;
import me.retrodaredevil.solarthing.actions.environment.LatestPacketGroupEnvironment;
import me.retrodaredevil.solarthing.config.message.MessageEventNode;
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
import me.retrodaredevil.solarthing.packets.collection.parsing.*;
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
import java.util.*;

public class MessageSenderMain {
	private MessageSenderMain() { throw new UnsupportedOperationException(); }

	private static final Logger LOGGER = LoggerFactory.getLogger(MessageSenderMain.class);
	private static final ObjectMapper CONFIG_MAPPER = JacksonUtil.defaultMapper();
	private static final ObjectMapper PARSE_MAPPER = JacksonUtil.lenientMapper(JacksonUtil.defaultMapper());

	private static Map<String, MessageSender> getMessageSenderMap(MessageSenderProgramOptions options) throws IOException {
		Map<String, MessageSender> senderMap = new HashMap<>();
		for (Map.Entry<String, File> entry : options.getMessageSenderFileMap().entrySet()) {
			String key = entry.getKey();
			File file = entry.getValue();
			MessageSender sender = CONFIG_MAPPER.readValue(file, MessageSender.class);
			senderMap.put(key, sender);
		}
		return senderMap;
	}

	public static int startMessageSender(MessageSenderProgramOptions options) {
		LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Starting message program.");
		LOGGER.debug("Note that this has to get packets twice before this sends anything.");
		final Map<String, MessageSender> messageSenderMap;
		try {
			messageSenderMap = getMessageSenderMap(options);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		List<MessageEventNode> messageEventNodes = options.getMessageEventNodes();
		ActionNode actionNode = new ActionNode() {
			FragmentedPacketGroup last = null;
			@Override
			public Action createAction(ActionEnvironment actionEnvironment) {
				LatestPacketGroupEnvironment latestPacketGroupEnvironment = actionEnvironment.getInjectEnvironment().get(LatestPacketGroupEnvironment.class);
				return Actions.createRunOnce(() -> {
					FragmentedPacketGroup packetGroup = (FragmentedPacketGroup) latestPacketGroupEnvironment.getPacketGroupProvider().getPacketGroup();
					FragmentedPacketGroup last = this.last;
					this.last = packetGroup;
					if (last != null) {
						for (MessageEventNode messageEventNode : messageEventNodes) {
							List<MessageSender> messageSenders = new ArrayList<>();
							for (String senderName : messageEventNode.getSendTo()) {
								MessageSender sender = messageSenderMap.get(senderName);
								if (sender == null) {
									throw new IllegalArgumentException("senderName: " + senderName + " is not defined!");
								}
								messageSenders.add(sender);
							}
							MessageSender sender = new MessageSenderMultiplexer(messageSenders);
							messageEventNode.getMessageEvent().run(sender, last, packetGroup);
						}
					}
				});
			}
		};
		return AutomationMain.startAutomation(Collections.singletonList(actionNode), options);
	}
}

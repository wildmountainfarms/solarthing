package me.retrodaredevil.solarthing.actions.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.solarthing.actions.ActionNode;
import me.retrodaredevil.solarthing.actions.environment.ActionEnvironment;
import me.retrodaredevil.solarthing.actions.environment.EventDatabaseCacheEnvironment;
import me.retrodaredevil.solarthing.actions.environment.LatestFragmentedPacketGroupEnvironment;
import me.retrodaredevil.solarthing.config.FileMapper;
import me.retrodaredevil.solarthing.config.message.MessageEventNode;
import me.retrodaredevil.solarthing.database.cache.ProcessedPacketTracker;
import me.retrodaredevil.solarthing.message.MessageSender;
import me.retrodaredevil.solarthing.message.MessageSenderMultiplexer;
import me.retrodaredevil.solarthing.packets.collection.*;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@JsonTypeName("message-sender")
public class MessageSenderActionNode implements ActionNode {
	private static final Logger LOGGER = LoggerFactory.getLogger(MessageSenderActionNode.class);

	private static final ObjectMapper CONFIG_MAPPER = JacksonUtil.defaultMapper();

	private final Map<String, MessageSender> messageSenderMap;
	private final List<MessageEventNode> messageEventNodes;

	private FragmentedPacketGroup last = null;
	private final ProcessedPacketTracker eventProcessedPacketTracker = new ProcessedPacketTracker();

	public MessageSenderActionNode(Map<String, MessageSender> messageSenderMap, List<MessageEventNode> messageEventNodes) {
		this.messageSenderMap = messageSenderMap;
		this.messageEventNodes = messageEventNodes;
	}

	@JsonCreator
	public static MessageSenderActionNode create(
			@JsonProperty("senders") Map<String, String> messageSenderFileMap,
			@JsonProperty("events") List<MessageEventNode> messageEventNodes
//			@JacksonInject(FileMapper.JACKSON_INJECT_IDENTIFIER) FileMapper fileMapper // TODO in future, add this back hoping that https://github.com/FasterXML/jackson-databind/issues/3072 gets implemented
	) throws IOException {
		final Map<String, MessageSender> messageSenderMap = getMessageSenderMap(
				messageSenderFileMap,
//				fileMapper == null ? FileMapper.ONE_TO_ONE : fileMapper
				FileMapper.ONE_TO_ONE
		);
		return new MessageSenderActionNode(messageSenderMap, messageEventNodes);
	}
	private static Map<String, MessageSender> getMessageSenderMap(Map<String, String> messageSenderFileMap, FileMapper fileMapper) throws IOException {
		requireNonNull(messageSenderFileMap);
		requireNonNull(fileMapper);
		Map<String, MessageSender> senderMap = new HashMap<>();
		for (Map.Entry<String, String> entry : messageSenderFileMap.entrySet()) {
			String key = entry.getKey();
			File file = fileMapper.map(entry.getValue());
			MessageSender sender = CONFIG_MAPPER.readValue(file, MessageSender.class);
			senderMap.put(key, sender);
		}
		return senderMap;
	}

	private MessageSender getMessageSenderFrom(MessageEventNode messageEventNode) {
		List<MessageSender> messageSenders = new ArrayList<>();
		for (String senderName : messageEventNode.getSendTo()) {
			MessageSender sender = messageSenderMap.get(senderName);
			if (sender == null) {
				throw new IllegalArgumentException("senderName: " + senderName + " is not defined!");
			}
			messageSenders.add(sender);
		}
		return new MessageSenderMultiplexer(messageSenders);
	}

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		LatestFragmentedPacketGroupEnvironment latestPacketGroupEnvironment = actionEnvironment.getInjectEnvironment().get(LatestFragmentedPacketGroupEnvironment.class);
		EventDatabaseCacheEnvironment eventDatabaseCacheEnvironment = actionEnvironment.getInjectEnvironment().get(EventDatabaseCacheEnvironment.class);
		return Actions.createRunOnce(() -> {
			FragmentedPacketGroup packetGroup = latestPacketGroupEnvironment.getFragmentedPacketGroupProvider().getPacketGroup();
			FragmentedPacketGroup last = this.last;
			this.last = packetGroup;
			if (packetGroup == null) {
				LOGGER.warn("packetGroup is null!");
			}
			final boolean statusRun;
			if (packetGroup != null && last != null) {
				if (packetGroup.getDateMillis() <= last.getDateMillis()) {
					LOGGER.debug("No new packet group! Will not try to send any messages based on status packets.");
					statusRun = false;
				} else {
					statusRun = true;
				}
			} else {
				LOGGER.debug("No last packet group yet (this is normal). Will not send any messages until next iteration.");
				statusRun = false;
			}

			long afterDateMillis = Instant.now().minusSeconds(60).toEpochMilli(); // Only process stuff from the last minute
			List<InstancePacketGroup> unhandledEventInstancePacketGroups = eventProcessedPacketTracker.getUnprocessedPackets(eventDatabaseCacheEnvironment.getEventDatabaseCache(), afterDateMillis).stream()
					.map(storedPacketGroup -> {
						InstancePacketGroup instancePacketGroup = PacketGroups.parseToInstancePacketGroup(storedPacketGroup, DefaultInstanceOptions.REQUIRE_NO_DEFAULTS);
						DefaultInstanceOptions.requireNoDefaults(instancePacketGroup); // all new packets should have a source and fragment ID in them. (Most old ones too by now)
						return instancePacketGroup;
					})
					.collect(Collectors.toList());

			for (MessageEventNode messageEventNode : messageEventNodes) {
				MessageSender sender = getMessageSenderFrom(messageEventNode);
				if (statusRun) {
					messageEventNode.getMessageEvent().run(sender, last, packetGroup);
				}
				for (InstancePacketGroup instancePacketGroup : unhandledEventInstancePacketGroups) {
					messageEventNode.getMessageEvent().runForEvent(sender, instancePacketGroup);
				}
			}
		});
	}
}

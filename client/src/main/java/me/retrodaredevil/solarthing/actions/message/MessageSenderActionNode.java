package me.retrodaredevil.solarthing.actions.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.solarthing.actions.environment.EventDatabaseCacheEnvironment;
import me.retrodaredevil.solarthing.actions.environment.LatestFragmentedPacketGroupEnvironment;
import me.retrodaredevil.solarthing.config.FileMapper;
import me.retrodaredevil.solarthing.config.message.MessageEventNode;
import me.retrodaredevil.solarthing.database.cache.ProcessedPacketTracker;
import me.retrodaredevil.solarthing.message.MessageSender;
import me.retrodaredevil.solarthing.message.MessageSenderMultiplexer;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.packets.collection.InstancePacketGroup;
import me.retrodaredevil.solarthing.packets.collection.PacketGroups;
import me.retrodaredevil.solarthing.program.ConfigUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
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
	) {
		final Map<String, MessageSender> messageSenderMap = getMessageSenderMap(
				messageSenderFileMap,
//				fileMapper == null ? FileMapper.ONE_TO_ONE : fileMapper
				FileMapper.ONE_TO_ONE
		);
		return new MessageSenderActionNode(messageSenderMap, messageEventNodes);
	}
	private static Map<String, MessageSender> getMessageSenderMap(Map<String, String> messageSenderFileMap, FileMapper fileMapper) {
		requireNonNull(messageSenderFileMap);
		requireNonNull(fileMapper);
		Map<String, MessageSender> senderMap = new HashMap<>();
		for (Map.Entry<String, String> entry : messageSenderFileMap.entrySet()) {
			String key = entry.getKey();
			Path file = fileMapper.map(entry.getValue());
			MessageSender sender = ConfigUtil.readConfig(file, MessageSender.class);
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
			List<InstancePacketGroup> unhandledEventInstancePacketGroups = eventProcessedPacketTracker.getUnprocessedPackets(eventDatabaseCacheEnvironment.getEventDatabaseCacheManager(), afterDateMillis).stream()
					.map(PacketGroups::parseToInstancePacketGroupRequireNoDefaults)
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

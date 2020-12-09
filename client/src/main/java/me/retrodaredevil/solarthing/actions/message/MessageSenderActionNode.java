package me.retrodaredevil.solarthing.actions.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.solarthing.actions.ActionNode;
import me.retrodaredevil.solarthing.actions.environment.ActionEnvironment;
import me.retrodaredevil.solarthing.actions.environment.LatestPacketGroupEnvironment;
import me.retrodaredevil.solarthing.config.message.MessageEventNode;
import me.retrodaredevil.solarthing.message.MessageSender;
import me.retrodaredevil.solarthing.message.MessageSenderMultiplexer;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.util.JacksonUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonTypeName("message-sender")
public class MessageSenderActionNode implements ActionNode {

	private static final ObjectMapper CONFIG_MAPPER = JacksonUtil.defaultMapper();

	private final Map<String, MessageSender> messageSenderMap;
	private final List<MessageEventNode> messageEventNodes;

	private FragmentedPacketGroup last = null;

	public MessageSenderActionNode(Map<String, MessageSender> messageSenderMap, List<MessageEventNode> messageEventNodes) {
		this.messageSenderMap = messageSenderMap;
		this.messageEventNodes = messageEventNodes;
	}

	@JsonCreator
	public static MessageSenderActionNode create(
			@JsonProperty("senders") Map<String, File> messageSenderFileMap,
			@JsonProperty("events") List<MessageEventNode> messageEventNodes) throws IOException {
		final Map<String, MessageSender> messageSenderMap = getMessageSenderMap(messageSenderFileMap);
		return new MessageSenderActionNode(messageSenderMap, messageEventNodes);
	}
	private static Map<String, MessageSender> getMessageSenderMap(Map<String, File> messageSenderFileMap) throws IOException {
		Map<String, MessageSender> senderMap = new HashMap<>();
		for (Map.Entry<String, File> entry : messageSenderFileMap.entrySet()) {
			String key = entry.getKey();
			File file = entry.getValue();
			MessageSender sender = CONFIG_MAPPER.readValue(file, MessageSender.class);
			senderMap.put(key, sender);
		}
		return senderMap;
	}

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
}

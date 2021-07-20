package me.retrodaredevil.solarthing.chatbot;

import info.debatty.java.stringsimilarity.JaroWinkler;
import me.retrodaredevil.solarthing.FragmentedPacketGroupProvider;
import me.retrodaredevil.solarthing.commands.CommandInfo;
import me.retrodaredevil.solarthing.commands.packets.status.AvailableCommandsPacket;
import me.retrodaredevil.solarthing.message.MessageSender;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class CommandChatBotHandler implements ChatBotHandler {
	private final Map<String, List<String>> permissionMap;
	private final FragmentedPacketGroupProvider packetGroupProvider;

	private final PermissionHandler permissionHandler = new PermissionHandler();

	public CommandChatBotHandler(Map<String, List<String>> permissionMap, FragmentedPacketGroupProvider packetGroupProvider) {
		requireNonNull(this.permissionMap = permissionMap);
		requireNonNull(this.packetGroupProvider = packetGroupProvider);
	}

	private List<AvailableCommand> getCommands() {
		FragmentedPacketGroup packetGroup = packetGroupProvider.getPacketGroup();
		return packetGroup.getPackets().stream()
				.filter(packet -> packet instanceof AvailableCommandsPacket)
				.map(packet -> (AvailableCommandsPacket) packet)
				.flatMap(packet -> {
					int fragmentId = packetGroup.getFragmentId(packet);
					return packet.getCommandInfoList().stream()
							.map(commandInfo -> new AvailableCommand(fragmentId, commandInfo));
				})
				.collect(Collectors.toList());
	}

	@Override
	public boolean handleMessage(Message message, MessageSender messageSender) {
		List<String> permissions = permissionMap.getOrDefault(message.getUserId(), Collections.emptyList());
		List<AvailableCommand> availableCommands = getCommands()
				.stream()
				.filter(availableCommand -> permissions.stream().anyMatch(permission -> permissionHandler.permissionMatches(permission, availableCommand.getPermission())))
				.collect(Collectors.toList());
		AvailableCommand best = ChatBotUtil.findBest(availableCommands, availableCommand -> availableCommand.getCommandInfo().getDisplayName(), message.getText());
		if (best == null) {
			return false;
		}
		CommandInfo info = best.getCommandInfo();
		messageSender.sendMessage("You request command: " + info.getName() + " AKA: " + info.getDisplayName());
		return true;
	}
}
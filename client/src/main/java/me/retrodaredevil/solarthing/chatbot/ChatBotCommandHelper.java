package me.retrodaredevil.solarthing.chatbot;

import me.retrodaredevil.solarthing.FragmentedPacketGroupProvider;
import me.retrodaredevil.solarthing.commands.util.CommandManager;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.commands.packets.status.AvailableCommandsPacket;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class ChatBotCommandHelper {
	private static final Logger LOGGER = LoggerFactory.getLogger(ChatBotCommandHelper.class);

	private final Map<String, List<String>> permissionMap;
	private final FragmentedPacketGroupProvider packetGroupProvider;
	private final CommandManager commandManager;

	private final PermissionHandler permissionHandler = new PermissionHandler();

	public ChatBotCommandHelper(Map<String, List<String>> permissionMap, FragmentedPacketGroupProvider packetGroupProvider, CommandManager commandManager) {
		requireNonNull(this.permissionMap = permissionMap);
		requireNonNull(this.packetGroupProvider = packetGroupProvider);
		requireNonNull(this.commandManager = commandManager);
	}
	private List<AvailableCommand> getCommands() {
		FragmentedPacketGroup packetGroup = packetGroupProvider.getPacketGroup();

		if (packetGroup == null) {
			LOGGER.warn("getCommands() was called, but the packetGroupProvider gave a null value!");
			return Collections.emptyList();
		}
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

	public List<AvailableCommand> getAllowedCommands(Message message) {
		List<String> permissions = permissionMap.getOrDefault(message.getUserId(), Collections.emptyList());
		return getCommands()
				.stream()
				.filter(availableCommand -> hasPermission(message, availableCommand.getPermission(), permissions))
				.collect(Collectors.toList());
	}
	public boolean hasPermission(Message message, String permissionString) {
		List<String> permissions = permissionMap.getOrDefault(message.getUserId(), Collections.emptyList());
		return hasPermission(message, permissionString, permissions);
	}
	private boolean hasPermission(Message message, String permissionString, List<String> permissions) {
		return permissions.stream().anyMatch(permission -> permissionHandler.permissionMatches(permission, permissionString));
	}

	public CommandManager getCommandManager() {
		return commandManager;
	}

	/**
	 * @param message Used to determine the commands the user is allowed to send
	 * @param text Used to find the desired command from the commands available to the user
	 * @return The best available command, or null if none of the commands available to the user were allowed
	 */
	public @Nullable AvailableCommand getBestCommand(Message message, String text) {
		List<AvailableCommand> availableCommands = getAllowedCommands(message);
		return ChatBotUtil.findBest(availableCommands, availableCommand -> availableCommand.getCommandInfo().getDisplayName(), text);
	}
}

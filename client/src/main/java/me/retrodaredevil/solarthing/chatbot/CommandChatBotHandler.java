package me.retrodaredevil.solarthing.chatbot;

import me.retrodaredevil.solarthing.FragmentedPacketGroupProvider;
import me.retrodaredevil.solarthing.actions.command.CommandManager;
import me.retrodaredevil.solarthing.actions.environment.ActionEnvironment;
import me.retrodaredevil.solarthing.actions.environment.SolarThingDatabaseEnvironment;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.commands.CommandInfo;
import me.retrodaredevil.solarthing.commands.packets.open.ImmutableRequestCommandPacket;
import me.retrodaredevil.solarthing.commands.packets.status.AvailableCommandsPacket;
import me.retrodaredevil.solarthing.database.SolarThingDatabase;
import me.retrodaredevil.solarthing.database.exception.SolarThingDatabaseException;
import me.retrodaredevil.solarthing.message.MessageSender;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class CommandChatBotHandler implements ChatBotHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(CommandChatBotHandler.class);

	private final Map<String, List<String>> permissionMap;
	private final FragmentedPacketGroupProvider packetGroupProvider;
	private final CommandManager commandManager;
	private final Supplier<ActionEnvironment> actionEnvironmentSupplier;

	private final PermissionHandler permissionHandler = new PermissionHandler();
	private final ExecutorService executorService = Executors.newSingleThreadExecutor();

	public CommandChatBotHandler(Map<String, List<String>> permissionMap, FragmentedPacketGroupProvider packetGroupProvider, CommandManager commandManager, Supplier<ActionEnvironment> actionEnvironmentSupplier) {
		requireNonNull(this.permissionMap = permissionMap);
		requireNonNull(this.packetGroupProvider = packetGroupProvider);
		requireNonNull(this.commandManager = commandManager);
		requireNonNull(this.actionEnvironmentSupplier = actionEnvironmentSupplier);
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

	private List<AvailableCommand> getAllowedCommands(Message message) {
		List<String> permissions = permissionMap.getOrDefault(message.getUserId(), Collections.emptyList());
		return getCommands()
				.stream()
				.filter(availableCommand -> permissions.stream().anyMatch(permission -> permissionHandler.permissionMatches(permission, availableCommand.getPermission())))
				.collect(Collectors.toList());
	}

	@Override
	public boolean handleMessage(Message message, MessageSender messageSender) {
		List<AvailableCommand> availableCommands = getAllowedCommands(message);
		AvailableCommand best = ChatBotUtil.findBest(availableCommands, availableCommand -> availableCommand.getCommandInfo().getDisplayName(), message.getText());
		if (best == null) {
			return false;
		}
		CommandInfo info = best.getCommandInfo();
		messageSender.sendMessage("Sending command: " + info.getDisplayName());
		ActionEnvironment actionEnvironment = requireNonNull(actionEnvironmentSupplier.get(), "No ActionEnvironment!");
		SolarThingDatabase database = actionEnvironment.getInjectEnvironment().get(SolarThingDatabaseEnvironment.class).getSolarThingDatabase();
		PacketCollection packetCollection = commandManager.create(
				actionEnvironment,
				Collections.singleton(best.getFragmentId()),
				new ImmutableRequestCommandPacket(info.getName())
		);
		executorService.execute(() -> {
			try {
				database.getOpenDatabase().uploadPacketCollection(packetCollection, null);
				LOGGER.info("Uploaded command request document");
			} catch (SolarThingDatabaseException e) {
				LOGGER.error("Error while uploading document.", e);
				messageSender.sendMessage("Failed to upload command: " + info.getDisplayName());
			}
		});
		return true;
	}

	@Override
	public @NotNull List<String> getHelpLines(Message helpMessage) {
		List<AvailableCommand> availableCommands = getAllowedCommands(helpMessage);
		return availableCommands.stream()
				.map(availableCommand -> '"' + availableCommand.getCommandInfo().getDisplayName() + "\" (" + availableCommand.getFragmentId() + ") -- " + availableCommand.getCommandInfo().getDescription())
				.collect(Collectors.toList());
	}
}

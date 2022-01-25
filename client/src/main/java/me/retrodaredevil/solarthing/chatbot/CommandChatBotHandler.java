package me.retrodaredevil.solarthing.chatbot;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.commands.CommandInfo;
import me.retrodaredevil.solarthing.commands.packets.open.ImmutableRequestCommandPacket;
import me.retrodaredevil.solarthing.database.SolarThingDatabase;
import me.retrodaredevil.solarthing.database.exception.SolarThingDatabaseException;
import me.retrodaredevil.solarthing.message.MessageSender;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.collection.PacketCollectionCreator;
import me.retrodaredevil.solarthing.packets.collection.PacketCollectionIdGenerator;
import me.retrodaredevil.solarthing.packets.instance.InstanceTargetPackets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class CommandChatBotHandler implements ChatBotHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(CommandChatBotHandler.class);

	private final ChatBotCommandHelper commandHelper;
	private final SolarThingDatabase database;
	private final String sourceId;
	private final ZoneId zoneId;
	private final ExecutorService executorService = Executors.newSingleThreadExecutor();

	public CommandChatBotHandler(ChatBotCommandHelper commandHelper, SolarThingDatabase database, String sourceId, ZoneId zoneId) {
		requireNonNull(this.commandHelper = commandHelper);
		requireNonNull(this.database = database);
		requireNonNull(this.sourceId = sourceId);
		requireNonNull(this.zoneId = zoneId);
	}

	@Override
	public boolean handleMessage(Message message, MessageSender messageSender) {
		AvailableCommand best = commandHelper.getBestCommand(message, message.getText());
		if (best == null) {
			return false;
		}
		CommandInfo info = best.getCommandInfo();
		messageSender.sendMessage("Sending command: " + info.getDisplayName());
		PacketCollectionCreator creator = commandHelper.getCommandManager().makeCreator(
				sourceId,
				zoneId,
				InstanceTargetPackets.create(Collections.singleton(best.getFragmentId())),
				new ImmutableRequestCommandPacket(info.getName()),
				PacketCollectionIdGenerator.Defaults.UNIQUE_GENERATOR
		);
		Instant now = Instant.now();
		PacketCollection packetCollection = creator.create(now);
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
		List<AvailableCommand> availableCommands = commandHelper.getAllowedCommands(helpMessage);
		return availableCommands.stream()
				.map(availableCommand -> '"' + availableCommand.getCommandInfo().getDisplayName() + "\" (" + availableCommand.getFragmentId() + ") -- " + availableCommand.getCommandInfo().getDescription())
				.collect(Collectors.toList());
	}
}

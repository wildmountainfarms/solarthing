package me.retrodaredevil.solarthing.chatbot;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.commands.packets.open.ImmutableScheduleCommandPacket;
import me.retrodaredevil.solarthing.database.SolarThingDatabase;
import me.retrodaredevil.solarthing.database.exception.SolarThingDatabaseException;
import me.retrodaredevil.solarthing.message.MessageSender;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.collection.PacketCollectionCreator;
import me.retrodaredevil.solarthing.packets.collection.PacketCollectionIdGenerator;
import me.retrodaredevil.solarthing.type.alter.packets.ScheduledCommandData;
import me.retrodaredevil.solarthing.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.Objects.requireNonNull;

public class ScheduleCommandChatBotHandler implements ChatBotHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleCommandChatBotHandler.class);
	private static final String SHORT_USAGE = "schedule <command> <in|at|for> <duration>";
	private static final String USAGE = "Incorrect usage of schedule! Usage:\n\t" + SHORT_USAGE;

	private final ChatBotCommandHelper commandHelper;
	private final SolarThingDatabase database;
	private final String sourceId;
	private final ZoneId zoneId;


	private final ExecutorService executorService = Executors.newSingleThreadExecutor();

	public ScheduleCommandChatBotHandler(ChatBotCommandHelper commandHelper, SolarThingDatabase database, String sourceId, ZoneId zoneId) {
		requireNonNull(this.commandHelper = commandHelper);
		requireNonNull(this.database = database);
		requireNonNull(this.sourceId = sourceId);
		requireNonNull(this.zoneId = zoneId);
	}

	private boolean canScheduleAnyCommands(Message message) {
		return commandHelper.hasPermission(message, "solarthing.schedule");
	}

	@Override
	public boolean handleMessage(Message message, MessageSender messageSender) {
		String text = message.getText().toLowerCase(Locale.ENGLISH);
		String[] split = text.split(" ");
		if (split.length == 0) {
			return false;
		}
		if (split[0].equals("schedule")) {
			// We only have to check for permissions here
			if (!canScheduleAnyCommands(message)) {
				messageSender.sendMessage("You do not have permission to schedule commands!");
				return true;
			}
			if (split.length == 1) {
				messageSender.sendMessage(USAGE);
				return true;
			}
			String remaining = text.replace("schedule", "").trim();
			String[] result = remaining.split(" at | in | for ", 2);
			if (result.length != 2) {
				messageSender.sendMessage(USAGE);
				return true;
			}
			final String command = result[0].trim();
			final String time = result[1].trim();
			AvailableCommand availableCommand = commandHelper.getBestCommand(message, command);
			if (availableCommand == null) {
				messageSender.sendMessage("Unknown command: " + command);
				return true;
			}
			Duration timeInFutureToSchedule = TimeUtil.lenientParseDurationOrNull(time);

			if (timeInFutureToSchedule == null) {
				// only support duration in future for now
				messageSender.sendMessage("Unsupported time format: '" + time + "'");
			} else {
				Instant now = Instant.now();
				schedule(messageSender, now, now.plus(timeInFutureToSchedule), availableCommand);
			}
			return true;
		}

		return false;
	}
	private void schedule(MessageSender messageSender, Instant now, Instant targetTime, AvailableCommand availableCommand) {
		if (now.plus(Duration.ofMinutes(1)).isAfter(targetTime)) {
			messageSender.sendMessage("Cannot schedule a command less than one minute from now.");
			return;
		}
		if (now.plus(Duration.ofHours(72)).isBefore(targetTime)) {
			messageSender.sendMessage("Cannot schedule a command more than 72 hours from now.");
			return;
		}

		UUID uniqueId = UUID.randomUUID();
		PacketCollectionCreator creator = commandHelper.getCommandManager().makeCreator(
				sourceId,
				zoneId,
				null, // We don't have an InstanceTargetPacket because scheduling commands is not handled by a program with a fragment ID // also look at PacketGroups.parseToTargetPacketGroup() for interpretation without a TargetInstancePacket
				new ImmutableScheduleCommandPacket(new ScheduledCommandData(
						targetTime.toEpochMilli(),
						availableCommand.getCommandInfo().getName(),
						Collections.singleton(availableCommand.getFragmentId())
				), uniqueId),
				PacketCollectionIdGenerator.Defaults.UNIQUE_GENERATOR
		);
		PacketCollection packetCollection = creator.create(now);

		messageSender.sendMessage("Scheduling " + availableCommand.getCommandInfo().getDisplayName() + " at " + TimeUtil.instantToSlackDateSeconds(targetTime));

		executorService.execute(() -> {
			boolean success = false;
			try {
				database.getOpenDatabase().uploadPacketCollection(packetCollection, null);
				success = true;
			} catch (SolarThingDatabaseException e) {
				LOGGER.error("Could not upload schedule command packet collection", e);
			}
			if (success) {
				messageSender.sendMessage("Successfully requested schedule. ID: " + uniqueId);
			} else {
				messageSender.sendMessage("Could not upload schedule command request.");
			}
		});
	}

	@Override
	public @NotNull List<String> getHelpLines(Message helpMessage) {
		if (canScheduleAnyCommands(helpMessage)) {
			return Collections.singletonList(SHORT_USAGE);
		}
		return Collections.emptyList();
	}
}

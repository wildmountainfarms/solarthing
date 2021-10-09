package me.retrodaredevil.solarthing.chatbot;

import me.retrodaredevil.solarthing.actions.command.CommandManager;
import me.retrodaredevil.solarthing.actions.environment.InjectEnvironment;
import me.retrodaredevil.solarthing.actions.environment.SolarThingDatabaseEnvironment;
import me.retrodaredevil.solarthing.commands.packets.open.ImmutableScheduleCommandPacket;
import me.retrodaredevil.solarthing.database.SolarThingDatabase;
import me.retrodaredevil.solarthing.message.MessageSender;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.type.alter.packets.ScheduledCommandData;
import me.retrodaredevil.solarthing.util.TimeUtil;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.UUID;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public class ScheduleCommandChatBotHandler implements ChatBotHandler {
	private static final String USAGE = "Incorrect usage of schedule! Usage: \n\tschedule <command> <at <time>|in <duration>>";
	private final ChatBotCommandHelper commandHelper;
	private final Supplier<InjectEnvironment> injectEnvironmentSupplier;

	public ScheduleCommandChatBotHandler(ChatBotCommandHelper commandHelper, Supplier<InjectEnvironment> injectEnvironmentSupplier) {
		requireNonNull(this.commandHelper = commandHelper);
		requireNonNull(this.injectEnvironmentSupplier = injectEnvironmentSupplier);
	}

	@Override
	public boolean handleMessage(Message message, MessageSender messageSender) {
		String text = message.getText().toLowerCase();
		String[] split = text.split(" ");
		if (split.length == 0) {
			return false;
		}
		if (split[0].equals("schedule")) {
			if (split.length == 1) {
				messageSender.sendMessage(USAGE);
				return true;
			}
			String remaining = text.replace("schedule", "").trim();
			String[] result = remaining.split(" at | in ", 2);
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
		InjectEnvironment injectEnvironment = requireNonNull(injectEnvironmentSupplier.get(), "No InjectEnvironment!");
		SolarThingDatabase database = injectEnvironment.get(SolarThingDatabaseEnvironment.class).getSolarThingDatabase();

		CommandManager.Creator creator = commandHelper.getCommandManager().makeCreator(
				injectEnvironment,
				null, // We don't have an InstanceTargetPacket because scheduling commands is not handled by a program with a fragment ID
				new ImmutableScheduleCommandPacket(new ScheduledCommandData(
						targetTime.toEpochMilli(),
						availableCommand.getCommandInfo().getName(),
						Collections.singleton(availableCommand.getFragmentId())
				), UUID.randomUUID()) // random UUID here for a random unique ID
		);
		PacketCollection packetCollection = creator.create(now);

		messageSender.sendMessage("(Pretend) Scheduling " + availableCommand.getCommandInfo().getDisplayName() + " at " + targetTime);
	}
}

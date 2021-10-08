package me.retrodaredevil.solarthing.chatbot;

import me.retrodaredevil.solarthing.message.MessageSender;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;

import static java.util.Objects.requireNonNull;

public class ScheduleCommandChatBotHandler implements ChatBotHandler {
	private static final String USAGE = "Incorrect usage of schedule! Usage: \n\tschedule <command> <at <time>|in <duration>>";
	private final ChatBotCommandHelper commandHelper;

	public ScheduleCommandChatBotHandler(ChatBotCommandHelper commandHelper) {
		requireNonNull(this.commandHelper = commandHelper);
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
//			boolean isAt = remaining.replace(command, "").trim().startsWith("at");
			String formalDuration = time.toUpperCase()
					.replaceAll("HOUR", "H")
					.replaceAll("MINUTES", "M")
					.replaceAll("MINUTE", "M");
			if (!time.startsWith("P")) { // Make the format of the string we send lenient
				if (time.contains("T")) {
					formalDuration = "P" + formalDuration;
				} else {
					formalDuration = "PT" + formalDuration;
				}
			}
			Duration timeInFutureToSchedule = null;
			try {
				timeInFutureToSchedule = Duration.parse(formalDuration);
			} catch (DateTimeParseException ignored){}

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
		messageSender.sendMessage("Scheduling " + availableCommand.getCommandInfo().getDisplayName() + " at " + targetTime);
	}
}

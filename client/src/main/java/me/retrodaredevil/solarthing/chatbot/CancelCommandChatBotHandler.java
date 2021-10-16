package me.retrodaredevil.solarthing.chatbot;

import me.retrodaredevil.solarthing.AlterPacketsProvider;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.database.VersionedPacket;
import me.retrodaredevil.solarthing.message.MessageSender;
import me.retrodaredevil.solarthing.reason.ExecutionReason;
import me.retrodaredevil.solarthing.reason.OpenSourceExecutionReason;
import me.retrodaredevil.solarthing.type.alter.AlterPacket;
import me.retrodaredevil.solarthing.type.alter.StoredAlterPacket;
import me.retrodaredevil.solarthing.type.alter.UniqueRequestIdContainer;
import me.retrodaredevil.solarthing.type.alter.packets.ScheduledCommandPacket;
import me.retrodaredevil.solarthing.type.open.OpenSourcePacket;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

public class CancelCommandChatBotHandler implements ChatBotHandler {
	private static final String SHORT_USAGE = "cancel <scheduling ID>";
	private static final String USAGE = "Incorrect usage of cancel! Usage:\n\t" + SHORT_USAGE;

	private final ChatBotCommandHelper commandHelper;
	private final AlterPacketsProvider alterPacketsProvider;

	public CancelCommandChatBotHandler(ChatBotCommandHelper commandHelper, AlterPacketsProvider alterPacketsProvider) {
		this.commandHelper = commandHelper;
		this.alterPacketsProvider = alterPacketsProvider;
	}

	private boolean canCancelAnyCommands(Message message) {
		return commandHelper.hasPermission(message, "solarthing.unschedule");
	}

	private static List<VersionedPacket<StoredAlterPacket>> findStoredPacketsWithSchedulingIdOrNull(Stream<? extends VersionedPacket<StoredAlterPacket>> storedAlterPackets, UUID schedulingId) {
		requireNonNull(schedulingId);
		// The most common way to schedule commands is using an OpenSourceExecutionReason with a packet that is a
		//   ScheduleCommandPacket, which has a unique ID in it. We will need to accept multiple
		//   ways to cancel a command, but for now, this is a great start.
		return storedAlterPackets.filter(versionedPacket -> {
			StoredAlterPacket storedAlterPacket = versionedPacket.getPacket();
			AlterPacket alterPacket = storedAlterPacket.getPacket();
			if (alterPacket instanceof ScheduledCommandPacket) {
				ExecutionReason executionReason = ((ScheduledCommandPacket) alterPacket).getExecutionReason();
				if (executionReason instanceof OpenSourceExecutionReason) {
					OpenSourcePacket openSourcePacket = ((OpenSourceExecutionReason) executionReason).getSource().getPacket();
					// Instead of using ScheduleCommandPacket here, might as well be more general and use UniqueRequestIdContainer.
					//   At the time of writing this code, there's not actually a reason for it, but hey, maybe using UniqueRequestContainerId over ScheduleCommandPacket
					//   will be useful in the future.
					return openSourcePacket instanceof UniqueRequestIdContainer && ((UniqueRequestIdContainer) openSourcePacket).getUniqueRequestId().equals(schedulingId);
				}
			}
			return false;
		}).collect(Collectors.toList());
	}
	private void cancelCommand(MessageSender messageSender, UUID schedulingId) {
		List<VersionedPacket<StoredAlterPacket>> packets = alterPacketsProvider.getPackets();
		if (packets == null) {
			messageSender.sendMessage("Unable to cancel commands, as we are unable to reach the alter database.");
			return;
		}
		List<VersionedPacket<StoredAlterPacket>> targets = findStoredPacketsWithSchedulingIdOrNull(packets.stream(), schedulingId);
		if (targets.isEmpty()) {
			messageSender.sendMessage("Unable to find a scheduled command that was scheduled with the ID of " + schedulingId);
		} else if (targets.size() > 1) {
			messageSender.sendMessage("Multiple packets corresponded to ID: " + schedulingId + ". Please report this error.");
		} else {
			VersionedPacket<StoredAlterPacket> target = targets.get(0);
			messageSender.sendMessage("Let's pretend we just cancelled: " + target.getPacket().getDbId());
		}
	}

	@Override
	public boolean handleMessage(Message message, MessageSender messageSender) {
		String text = message.getText().toLowerCase();
		String[] split = text.split(" ");
		if (split.length == 0) {
			return false;
		}
		if (split[0].equals("cancel")) {
			if (!canCancelAnyCommands(message)) {
				messageSender.sendMessage("You do not have permission to cancel commands!");
				return true;
			}
			if (split.length == 1) {
				messageSender.sendMessage(USAGE);
				return true;
			}
			String idString = split[1];
			UUID schedulingId = null;
			try {
				schedulingId = UUID.fromString(idString);
			} catch (IllegalArgumentException ignored) {}

			if (schedulingId == null) {
				messageSender.sendMessage("You entered an invalid scheduling ID. Could not parse: " + idString);
			} else {
				cancelCommand(messageSender, schedulingId);
			}

			return true;
		}
		return false;
	}

	@Override
	public @NotNull List<String> getHelpLines(Message helpMessage) {
		return Arrays.asList(
				"cancel <scheduling ID>"
		);
	}
}

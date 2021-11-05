package me.retrodaredevil.solarthing.chatbot;

import me.retrodaredevil.solarthing.AlterPacketsProvider;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.database.SolarThingDatabase;
import me.retrodaredevil.solarthing.database.VersionedPacket;
import me.retrodaredevil.solarthing.message.MessageSender;
import me.retrodaredevil.solarthing.type.alter.StoredAlterPacket;
import me.retrodaredevil.solarthing.type.alter.flag.FlagData;
import me.retrodaredevil.solarthing.type.alter.packets.FlagPacket;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class FlagCommandChatBotHandler implements ChatBotHandler {
	private static final String USAGE_SET = "flag set <flag name>";
	private static final String USAGE_CLEAR = "flag clear <flag name>";
	private static final String USAGE_LIST = "flag list";
	private static final String INCORRECT_USAGE = "Incorrect usage of flag! Usage:";
	private static final String USAGE = INCORRECT_USAGE + "\n\t" + USAGE_SET + "\n\t" + USAGE_CLEAR + "\n\t" + USAGE_LIST;

	private final ChatBotCommandHelper commandHelper;
	private final SolarThingDatabase database;
	private final String sourceId;
	private final ZoneId zoneId;
	private final AlterPacketsProvider alterPacketsProvider;

	private final ExecutorService executorService = Executors.newSingleThreadExecutor();

	public FlagCommandChatBotHandler(ChatBotCommandHelper commandHelper, SolarThingDatabase database, String sourceId, ZoneId zoneId, AlterPacketsProvider alterPacketsProvider) {
		this.commandHelper = commandHelper;
		this.database = database;
		this.sourceId = sourceId;
		this.zoneId = zoneId;
		this.alterPacketsProvider = alterPacketsProvider;
	}

	private boolean canEditFlags(Message message) {
		return commandHelper.hasPermission(message, "solarthing.flags");
	}

	private void listFlags(MessageSender messageSender) {
		List<VersionedPacket<StoredAlterPacket>> packets = alterPacketsProvider.getPackets();
		if (packets == null) {
			messageSender.sendMessage("Could not get alter packets");
		} else {
			List<FlagData> flagDataList = packets.stream()
					.map(versionedPacket -> versionedPacket.getPacket().getPacket())
					.filter(packet -> packet instanceof FlagPacket)
					.map(packet -> ((FlagPacket) packet).getFlagData())
					.collect(Collectors.toList());
			if (flagDataList.isEmpty()) {
				messageSender.sendMessage("No flag data");
			} else {
				messageSender.sendMessage(
						"Flags:\n" + flagDataList.stream().map(flagData -> flagData.getFlagName() + " " + flagData.getActivePeriod())
								.collect(Collectors.joining("\n"))
				);
			}
		}
	}

	@Override
	public boolean handleMessage(Message message, MessageSender messageSender) {
		String text = message.getText();
		String lowerText = text.toLowerCase();
		String[] split = lowerText.split(" ");
		if (split.length == 0) {
			return false;
		}
		if (split[0].equals("flag")) {
			if (split.length == 1) {
				messageSender.sendMessage(USAGE);
				return true;
			}
			if (split[1].equals("set") || split[1].equals("clear")) {
				boolean isSet = split[1].equals("set");
				if (split.length == 2) {
					messageSender.sendMessage(USAGE + "\n\t" + (isSet ? USAGE_SET : USAGE_CLEAR));
					return true;
				}
				String desiredFlagName = text.split(" ", 3)[2]; // should not fail since we have a split.length == 2 check
				messageSender.sendMessage("This is where we do something with this flag: " + desiredFlagName);
				if (canEditFlags(message)) {
					messageSender.sendMessage("You have permission to edit flags");
				} else {
					messageSender.sendMessage("You do not have permission to edit flags");
				}
			} else if (split[1].equals("list")) {
				// Anyone can list flags. No permission required.
				listFlags(messageSender);
			} else {
				messageSender.sendMessage(USAGE);
			}
			return true;
		}
		return false;
	}

	@Override
	public @NotNull List<String> getHelpLines(Message helpMessage) {
		if (!canEditFlags(helpMessage)) {
			return Collections.emptyList();
		}
		return Arrays.asList(
				USAGE_SET,
				USAGE_CLEAR,
				USAGE_LIST
		);
	}
}

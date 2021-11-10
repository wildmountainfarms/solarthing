package me.retrodaredevil.solarthing.chatbot;

import me.retrodaredevil.solarthing.AlterPacketsProvider;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.commands.packets.open.DeleteAlterPacket;
import me.retrodaredevil.solarthing.commands.packets.open.ImmutableDeleteAlterPacket;
import me.retrodaredevil.solarthing.commands.packets.open.ImmutableRequestFlagPacket;
import me.retrodaredevil.solarthing.commands.packets.open.RequestFlagPacket;
import me.retrodaredevil.solarthing.commands.util.CommandManager;
import me.retrodaredevil.solarthing.database.SolarThingDatabase;
import me.retrodaredevil.solarthing.database.VersionedPacket;
import me.retrodaredevil.solarthing.database.exception.SolarThingDatabaseException;
import me.retrodaredevil.solarthing.message.MessageSender;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.collection.PacketCollectionIdGenerator;
import me.retrodaredevil.solarthing.type.alter.AlterPacket;
import me.retrodaredevil.solarthing.type.alter.StoredAlterPacket;
import me.retrodaredevil.solarthing.type.alter.flag.FlagData;
import me.retrodaredevil.solarthing.type.alter.flag.TimeRangeActivePeriod;
import me.retrodaredevil.solarthing.type.alter.packets.FlagPacket;
import me.retrodaredevil.solarthing.util.TimeRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class FlagCommandChatBotHandler implements ChatBotHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(FlagCommandChatBotHandler.class);
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
				messageSender.sendMessage("There are no flags set.");
			} else {
				messageSender.sendMessage(
						"Flags:\n" + flagDataList.stream().map(flagData -> flagData.getFlagName() + " " + flagData.getActivePeriod())
								.collect(Collectors.joining("\n"))
				);
			}
		}
	}

	/**
	 * Note this method could return null or an empty list. Each have different meanings.
	 * @param flagName The flag name
	 * @return A list of versioned packets where each {@link StoredAlterPacket#getPacket()} is a {@link FlagPacket} with a {@link FlagData#getFlagName()} equal to {@code flagName}
	 *         or null if we could not get the alter packets.
	 */
	private @Nullable List<VersionedPacket<StoredAlterPacket>> getPacketsWithFlagName(String flagName) {
		List<VersionedPacket<StoredAlterPacket>> packets = alterPacketsProvider.getPackets();
		if (packets == null) {
			return null;
		}

		return packets.stream()
				.filter(versionedPacket -> {
					AlterPacket alterPacket = versionedPacket.getPacket().getPacket();
					return alterPacket instanceof FlagPacket && ((FlagPacket) alterPacket).getFlagData().getFlagName().equals(flagName);
				})
				.collect(Collectors.toList());
	}
	private void setFlag(MessageSender messageSender, String flagName) {
		Instant now = Instant.now();
		TimeRange timeRange = TimeRange.createAfter(now);
		FlagData data = new FlagData(flagName, new TimeRangeActivePeriod(timeRange));
		RequestFlagPacket requestFlagPacket = new ImmutableRequestFlagPacket(data);

		CommandManager.Creator creator = commandHelper.getCommandManager().makeCreator(sourceId, zoneId, null, requestFlagPacket, PacketCollectionIdGenerator.Defaults.UNIQUE_GENERATOR);
		PacketCollection packetCollection = creator.create(now);

		// TODO We should check if the flag being requested is already active.
		boolean success = false;
		try {
			database.getOpenDatabase().uploadPacketCollection(packetCollection, null);
			success = true;
		} catch (SolarThingDatabaseException e) {
			LOGGER.error("Could not upload request flag packet", e);
		}
		if (success) {
			messageSender.sendMessage("Successfully requested flag: '" + flagName + "' to be set.");
		} else {
			messageSender.sendMessage("Was unable to request flag set. See logs for details or try again.");
		}
	}
	private void clearFlag(MessageSender messageSender, String flagName) {
		List<VersionedPacket<StoredAlterPacket>> packets = getPacketsWithFlagName(flagName);
		if (packets == null) {
			messageSender.sendMessage("Could not get flags. Please try again.");
			return;
		}
		Instant now = Instant.now();
		if (packets.isEmpty()) {
			messageSender.sendMessage("Flag: '" + flagName + "' is not set!");
			return;
		}
		List<VersionedPacket<StoredAlterPacket>> activePackets = packets.stream().filter(versionedPacket -> {
			FlagPacket flagPacket = (FlagPacket) versionedPacket.getPacket().getPacket();
			return flagPacket.getFlagData().getActivePeriod().isActive(now);
		})
				.collect(Collectors.toList());
		if (activePackets.isEmpty()) {
			messageSender.sendMessage("Flag: '" + flagName + "' is not currently active. In a future update we may allow you to clear inactive flags.");
			return;
		}
		// TODO it's really not clear what we want to do if there are multiple active packets for the same flag.
		//   It's also pretty unclear on when someone would want that to happen, if at all.
		// We know that the user wants this flag cleared, so let's delete all the packets that are active.
		for (VersionedPacket<StoredAlterPacket> packetToRequestDeleteFor : activePackets) {
			DeleteAlterPacket deleteAlterPacket = new ImmutableDeleteAlterPacket(packetToRequestDeleteFor.getPacket().getDbId(), packetToRequestDeleteFor.getUpdateToken());
			CommandManager.Creator creator = commandHelper.getCommandManager().makeCreator(sourceId, zoneId, null, deleteAlterPacket, PacketCollectionIdGenerator.Defaults.UNIQUE_GENERATOR);
			PacketCollection packetCollection = creator.create(now);
			boolean success = false;
			try {
				database.getOpenDatabase().uploadPacketCollection(packetCollection, null);
				success = true;
			} catch (SolarThingDatabaseException e) {
				LOGGER.error("Could not upload request to delete alter packet for ID: " + packetToRequestDeleteFor.getPacket().getDbId(), e);
			}
			if (success) {
				messageSender.sendMessage("Requested delete for flag: '" + flagName + "' under ID: " + packetToRequestDeleteFor.getPacket().getDbId());
			} else {
				messageSender.sendMessage("Could not request delete for flag: '" + flagName + "' under ID: " + packetToRequestDeleteFor.getPacket().getDbId() + ". See logs for details.");
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
				if (canEditFlags(message)) {
					if (isSet) {
						setFlag(messageSender, desiredFlagName);
					} else {
						clearFlag(messageSender, desiredFlagName);
					}
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

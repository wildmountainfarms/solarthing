package me.retrodaredevil.solarthing.chatbot;

import me.retrodaredevil.solarthing.AlterPacketsProvider;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.commands.packets.open.CommandOpenPacket;
import me.retrodaredevil.solarthing.commands.packets.open.DeleteAlterPacket;
import me.retrodaredevil.solarthing.commands.packets.open.FlagAliasAddPacket;
import me.retrodaredevil.solarthing.commands.packets.open.ImmutableDeleteAlterPacket;
import me.retrodaredevil.solarthing.commands.packets.open.ImmutableFlagAliasAddPacket;
import me.retrodaredevil.solarthing.commands.packets.open.ImmutableRequestFlagPacket;
import me.retrodaredevil.solarthing.commands.packets.open.RequestFlagPacket;
import me.retrodaredevil.solarthing.database.SolarThingDatabase;
import me.retrodaredevil.solarthing.database.VersionedPacket;
import me.retrodaredevil.solarthing.database.exception.SolarThingDatabaseException;
import me.retrodaredevil.solarthing.message.MessageSender;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.collection.PacketCollectionCreator;
import me.retrodaredevil.solarthing.packets.collection.PacketCollectionIdGenerator;
import me.retrodaredevil.solarthing.type.alter.AlterPacket;
import me.retrodaredevil.solarthing.type.alter.StoredAlterPacket;
import me.retrodaredevil.solarthing.type.alter.flag.FlagAliasData;
import me.retrodaredevil.solarthing.type.alter.flag.FlagData;
import me.retrodaredevil.solarthing.type.alter.flag.TimeRangeActivePeriod;
import me.retrodaredevil.solarthing.type.alter.packets.FlagAliasPacket;
import me.retrodaredevil.solarthing.type.alter.packets.FlagPacket;
import me.retrodaredevil.solarthing.util.TimeRange;
import me.retrodaredevil.solarthing.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FlagCommandChatBotHandler implements ChatBotHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(FlagCommandChatBotHandler.class);
	private static final String USAGE_SET = "flag set <flag name> [for <duration>]";
	private static final String USAGE_CLEAR = "flag clear <flag name>";
	private static final String USAGE_LIST = "flag list";
	private static final String USAGE_ALIAS_ADD = "flag alias add <flag name> as <flag alias>";
	private static final String USAGE_ALIAS_DELETE = "flag alias delete <flag name>";
	private static final String INCORRECT_USAGE = "Incorrect usage of flag! Usage:";
	private static final String USAGE = INCORRECT_USAGE + "\n\t" + USAGE_SET + "\n\t" + USAGE_CLEAR
			+ "\n\t" + USAGE_ALIAS_ADD + "\n\t" + USAGE_ALIAS_DELETE + "\n\t" + USAGE_LIST;

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
				// TODO make active period prettier
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
	private @Nullable List<VersionedPacket<StoredAlterPacket>> getFlagPacketsWithFlagName(String flagName) {
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
	private @Nullable List<VersionedPacket<StoredAlterPacket>> getFlagAliasPacketsWithFlagName(String flagName) {
		List<VersionedPacket<StoredAlterPacket>> packets = alterPacketsProvider.getPackets();
		if (packets == null) {
			return null;
		}

		return packets.stream()
				.filter(versionedPacket -> {
					AlterPacket alterPacket = versionedPacket.getPacket().getPacket();
					return alterPacket instanceof FlagAliasPacket && ((FlagAliasPacket) alterPacket).getFlagAliasData().getFlagName().equals(flagName);
				})
				.collect(Collectors.toList());
	}
	private void uploadPacket(CommandOpenPacket commandOpenPacket, Consumer<@NotNull Boolean> onSendComplete) {
		PacketCollectionCreator creator = commandHelper.getCommandManager().makeCreator(sourceId, zoneId, null, commandOpenPacket, PacketCollectionIdGenerator.Defaults.UNIQUE_GENERATOR);

		// TODO We should check if the flag being requested is already active.
		executorService.execute(() -> {
			PacketCollection packetCollection = creator.create(Instant.now());
			boolean success = false;
			try {
				database.getOpenDatabase().uploadPacketCollection(packetCollection, null);
				success = true;
			} catch (SolarThingDatabaseException e) {
				LOGGER.error("Could not upload request flag packet", e);
			}
			onSendComplete.accept(success);
		});
	}
	private void setFlag(MessageSender messageSender, String flagName, @Nullable Duration duration) {
		Instant now = Instant.now();
		TimeRange timeRange = TimeRange.create(now, duration == null ? null : now.plus(duration));
		FlagData data = new FlagData(flagName, new TimeRangeActivePeriod(timeRange));
		RequestFlagPacket requestFlagPacket = new ImmutableRequestFlagPacket(data);

		// TODO We should check if the flag being requested is already active.
		uploadPacket(requestFlagPacket, success -> {
			if (success) {
				// TODO inform sender of end time if duration != null
				messageSender.sendMessage("Successfully requested flag: '" + flagName + "' to be set.");
			} else {
				messageSender.sendMessage("Was unable to request flag set. See logs for details or try again.");
			}
		});
	}
	private boolean requestDeleteAlter(Instant now, VersionedPacket<StoredAlterPacket> packetToRequestDeleteFor) {
		DeleteAlterPacket deleteAlterPacket = new ImmutableDeleteAlterPacket(packetToRequestDeleteFor.getPacket().getDbId(), packetToRequestDeleteFor.getUpdateToken());
		PacketCollectionCreator creator = commandHelper.getCommandManager().makeCreator(sourceId, zoneId, null, deleteAlterPacket, PacketCollectionIdGenerator.Defaults.UNIQUE_GENERATOR);
		PacketCollection packetCollection = creator.create(now);
		try {
			database.getOpenDatabase().uploadPacketCollection(packetCollection, null);
			return true;
		} catch (SolarThingDatabaseException e) {
			LOGGER.error("Could not upload request to delete alter packet for ID: " + packetToRequestDeleteFor.getPacket().getDbId(), e);
		}
		return false;
	}
	private void clearFlag(MessageSender messageSender, String flagName) {
		List<VersionedPacket<StoredAlterPacket>> packets = getFlagPacketsWithFlagName(flagName);
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
			boolean success = requestDeleteAlter(now, packetToRequestDeleteFor);
			if (success) {
				messageSender.sendMessage("Requested delete for flag: '" + flagName + "' under ID: " + packetToRequestDeleteFor.getPacket().getDbId());
			} else {
				messageSender.sendMessage("Could not request delete for flag: '" + flagName + "' under ID: " + packetToRequestDeleteFor.getPacket().getDbId() + ". See logs for details.");
			}
		}

	}

	private void addFlagAlias(MessageSender messageSender, String flagName, String flagAlias, @Nullable Duration defaultDuration) {
		FlagAliasData flagAliasData = new FlagAliasData(flagName, flagAlias, defaultDuration == null ? null : defaultDuration.toMillis());
		FlagAliasAddPacket flagAliasAddPacket = new ImmutableFlagAliasAddPacket(flagAliasData);

		uploadPacket(flagAliasAddPacket, success -> {
			if (success) {
				messageSender.sendMessage("Successfully requested to add alias for flag:  " + flagAliasAddPacket.getFlagAliasData().getFlagName());
			} else {
				messageSender.sendMessage("Was unable to add alias. See logs for details or try again.");
			}
		});
	}
	private void deleteFlagAlias(MessageSender messageSender, String flagName) {
		List<VersionedPacket<StoredAlterPacket>> packets = getFlagAliasPacketsWithFlagName(flagName);
		if (packets == null) {
			messageSender.sendMessage("Could not get flags. Please try again.");
			return;
		}
		if (packets.isEmpty()) {
			messageSender.sendMessage("Flag alias with flag name: '" + flagName + "' does not exist!");
			return;
		}
		Instant now = Instant.now();
		// We use a for loop because it's possible to add multiple aliases for a given command name,
		//   and we'll just delete all of them
		for (VersionedPacket<StoredAlterPacket> packetToRequestDeleteFor : packets) {
			boolean success = requestDeleteAlter(now, packetToRequestDeleteFor);
			if (success) {
				messageSender.sendMessage("Requested delete for flag alias with flag name: '" + flagName + "' under ID: " + packetToRequestDeleteFor.getPacket().getDbId());
			} else {
				messageSender.sendMessage("Could not request delete for flag alias with flag name: '" + flagName + "' under ID: " + packetToRequestDeleteFor.getPacket().getDbId() + ". See logs for details.");
			}
		}
		if (packets.isEmpty()) {
			messageSender.sendMessage("No flag aliases were found with flag name: " + flagName);
		}
	}
	private static Stream<FlagAliasPacket> findAliases(Stream<? extends VersionedPacket<StoredAlterPacket>> storedAlterPackets) {
		return storedAlterPackets
				.map(versionedPacket -> {
					StoredAlterPacket storedAlterPacket = versionedPacket.getPacket();
					AlterPacket alterPacket = storedAlterPacket.getPacket();
					if (alterPacket instanceof FlagAliasPacket) {
						return (FlagAliasPacket) alterPacket;
					}
					return null;
				})
				.filter(Objects::nonNull);
	}

	@Override
	public boolean handleMessage(Message message, MessageSender messageSender) {
		String text = message.getText();
		String lowerText = text.toLowerCase(Locale.ENGLISH);
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
					messageSender.sendMessage(INCORRECT_USAGE + "\n\t" + (isSet ? USAGE_SET : USAGE_CLEAR));
					return true;
				}
				// split.length >= 3
				String desiredFlagName = split[2]; // on 2022.07.10 we changed this to not allow spaces for simplicity. We may consider changing that
				if (canEditFlags(message)) {
					if (isSet) {
						final Duration duration;
						if (split.length >= 4) {
							if (split.length == 4 || !split[3].equals("for")) {
								messageSender.sendMessage(INCORRECT_USAGE + "\n\t" + USAGE_SET);
								return true;
							}
							// split.length >= 5
							String remaining = text.split(" ", 5)[4];
							duration = TimeUtil.lenientParseDurationOrNull(remaining);
							if (duration == null) {
								messageSender.sendMessage("Invalid duration: " + remaining);
								return true;
							}
						} else {
							duration = null;
						}

						setFlag(messageSender, desiredFlagName, duration);
					} else {
						clearFlag(messageSender, desiredFlagName);
					}
				} else {
					messageSender.sendMessage("You do not have permission to edit flags");
				}
				return true;
			} else if (split[1].equals("list")) {
				// Anyone can list flags. No permission required.
				listFlags(messageSender);
				return true;
			} else if (split[1].equals("alias")) {
				if (split.length <= 2) {
					messageSender.sendMessage(INCORRECT_USAGE + "\n\t" + USAGE_ALIAS_ADD + "\n\t" + USAGE_ALIAS_DELETE);
					return true;
				}
				if (split[2].equals("add")) {
					// flag alias add thing
					if (split.length <= 4) {
						messageSender.sendMessage(INCORRECT_USAGE + "\n\t" + USAGE_ALIAS_ADD);
						return true;
					}
					String remaining = text.split(" ", 4)[3];
					String[] nameSplit = remaining.split(" as ", 2);
					if (nameSplit.length != 2) {
						messageSender.sendMessage(INCORRECT_USAGE + "\n\t" + USAGE_ALIAS_ADD);
						return true;
					}
					String flagName = nameSplit[0];
					String[] aliasSplit = nameSplit[1].split(" for ", 2);
					assert aliasSplit.length == 1 || aliasSplit.length == 2;
					String flagAlias = aliasSplit[0];
					final Duration defaultDuration;
					if (aliasSplit.length == 1) {
						defaultDuration = null;
					} else {
						String durationString = aliasSplit[1];
						defaultDuration = TimeUtil.lenientParseDurationOrNull(durationString);
						if (defaultDuration == null) {
							messageSender.sendMessage("Invalid duration: " + durationString);
							return true;
						}
					}

					addFlagAlias(messageSender, flagName, flagAlias, defaultDuration);
					return true;
				} else if (split[2].equals("delete")) {
					if (split.length <= 3) {
						messageSender.sendMessage(INCORRECT_USAGE + "\n\t" + USAGE_ALIAS_DELETE);
						return true;
					}
					String flagName = split[3];
					deleteFlagAlias(messageSender, flagName);
					return true;
				} else {
					messageSender.sendMessage(INCORRECT_USAGE + "\n\t" + USAGE_ALIAS_ADD + "\n\t" + USAGE_ALIAS_DELETE);
					return true;
				}
			} else {
				messageSender.sendMessage(USAGE);
				return true;
			}
		} else {
			List<VersionedPacket<StoredAlterPacket>> packets = alterPacketsProvider.getPackets();
			if (packets == null) {
				// we can't see if there are any aliases available, so we can't do fuzzy matching for them
				return false;
			}
			Stream<FlagAliasPacket> flagAliasPackets = findAliases(packets.stream());
			FlagAliasPacket best = ChatBotUtil.findBest(flagAliasPackets.iterator(), flagAliasPacket -> flagAliasPacket.getFlagAliasData().getFlagAlias(), message.getText());
			if (best != null) {
				FlagAliasData data = best.getFlagAliasData();
				setFlag(messageSender, data.getFlagName(), data.getDefaultDuration());
				return true;
			}
			return false;
		}
	}

	private List<String> getAliasHelpLines() {
		List<VersionedPacket<StoredAlterPacket>> packets = alterPacketsProvider.getPackets();
		if (packets == null) {
			// we can't see if there are any aliases available, so we can't do fuzzy matching for them
			return Collections.emptyList();
		}
		return findAliases(packets.stream())
				.map(flagAliasPacket -> {
					FlagAliasData data = flagAliasPacket.getFlagAliasData();
					String extra = "";
					Duration defaultDuration = data.getDefaultDuration();
					if (defaultDuration != null) {
						extra = " for " + TimeUtil.millisToPrettyString(defaultDuration.toMillis());
					}
					return '"' + data.getFlagAlias() + "\" -- Sets flag " + data.getFlagName() + extra;
				})
				.collect(Collectors.toList());
	}

	@Override
	public @NotNull List<String> getHelpLines(Message helpMessage) {
		if (!canEditFlags(helpMessage)) {
			return Collections.emptyList();
		}
		List<String> result = new ArrayList<>(Arrays.asList(
				USAGE_SET,
				USAGE_CLEAR,
				USAGE_LIST,
				USAGE_ALIAS_ADD,
				USAGE_ALIAS_DELETE
		));
		result.addAll(getAliasHelpLines());
		return Collections.unmodifiableList(result);
	}
}

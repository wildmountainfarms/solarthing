package me.retrodaredevil.solarthing.actions.command;

import me.retrodaredevil.action.SimpleAction;
import me.retrodaredevil.solarthing.AlterPacketsProvider;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.commands.packets.open.CommandOpenPacket;
import me.retrodaredevil.solarthing.commands.packets.open.DeleteAlterPacket;
import me.retrodaredevil.solarthing.commands.packets.open.FlagAliasAddPacket;
import me.retrodaredevil.solarthing.commands.packets.open.ImmutableRequestCommandPacket;
import me.retrodaredevil.solarthing.commands.packets.open.RequestCommandPacket;
import me.retrodaredevil.solarthing.commands.packets.open.RequestFlagPacket;
import me.retrodaredevil.solarthing.commands.packets.open.ScheduleCommandPacket;
import me.retrodaredevil.solarthing.commands.util.CommandManager;
import me.retrodaredevil.solarthing.database.SolarThingDatabase;
import me.retrodaredevil.solarthing.database.VersionedPacket;
import me.retrodaredevil.solarthing.database.cache.DatabaseCache;
import me.retrodaredevil.solarthing.database.exception.IncompatibleUpdateTokenException;
import me.retrodaredevil.solarthing.database.exception.SolarThingDatabaseException;
import me.retrodaredevil.solarthing.database.exception.UpdateConflictSolarThingDatabaseException;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.collection.PacketCollectionCreator;
import me.retrodaredevil.solarthing.packets.collection.StoredPacketGroup;
import me.retrodaredevil.solarthing.packets.collection.TargetPacketGroup;
import me.retrodaredevil.solarthing.packets.instance.InstanceTargetPackets;
import me.retrodaredevil.solarthing.packets.security.LargeIntegrityPacket;
import me.retrodaredevil.solarthing.packets.security.crypto.Decrypt;
import me.retrodaredevil.solarthing.packets.security.crypto.DecryptException;
import me.retrodaredevil.solarthing.packets.security.crypto.InvalidKeyException;
import me.retrodaredevil.solarthing.packets.security.crypto.KeyUtil;
import me.retrodaredevil.solarthing.packets.security.crypto.PublicKeyLookUp;
import me.retrodaredevil.solarthing.program.SecurityPacketReceiver;
import me.retrodaredevil.solarthing.reason.ExecutionReason;
import me.retrodaredevil.solarthing.reason.OpenSourceExecutionReason;
import me.retrodaredevil.solarthing.type.alter.AlterPacket;
import me.retrodaredevil.solarthing.type.alter.ImmutableStoredAlterPacket;
import me.retrodaredevil.solarthing.type.alter.StoredAlterPacket;
import me.retrodaredevil.solarthing.type.alter.flag.ActivePeriod;
import me.retrodaredevil.solarthing.type.alter.flag.FlagAliasData;
import me.retrodaredevil.solarthing.type.alter.flag.FlagData;
import me.retrodaredevil.solarthing.type.alter.flag.TimeRangeActivePeriod;
import me.retrodaredevil.solarthing.type.alter.packets.FlagAliasPacket;
import me.retrodaredevil.solarthing.type.alter.packets.FlagPacket;
import me.retrodaredevil.solarthing.type.alter.packets.ScheduledCommandData;
import me.retrodaredevil.solarthing.type.alter.packets.ScheduledCommandPacket;
import me.retrodaredevil.solarthing.type.open.OpenSource;
import me.retrodaredevil.solarthing.util.TimeRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AlterManagerAction extends SimpleAction {
	private static final Logger LOGGER = LoggerFactory.getLogger(AlterManagerAction.class);
	private static final Cipher CIPHER;

	static {
		try {
			CIPHER = Cipher.getInstance(KeyUtil.CIPHER_TRANSFORMATION);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			throw new RuntimeException(e);
		}
	}
	/*
	I imagine this class is going to get pretty big. A refactor in the future is probably something that will be needed.
	For now, we will happily add code to this class until we decide something needs to change.
	 */

	private final ExecutorService executorService = Executors.newSingleThreadExecutor();


	private final CommandManager commandManager;
	private final SolarThingDatabase database;
	private final DatabaseCache openDatabaseCache;
	private final AlterPacketsProvider alterPacketsProvider;
	private final String sourceId;
	private final ZoneId zoneId;

	private final SecurityPacketReceiver securityPacketReceiver;

	private long listenStartTime;

	public AlterManagerAction(CommandManager commandManager, PublicKeyLookUp publicKeyLookUp, SolarThingDatabase database, DatabaseCache openDatabaseCache, AlterPacketsProvider alterPacketsProvider, String sourceId, ZoneId zoneId, int fragmentId) {
		super(false);
		this.commandManager = commandManager;
		this.database = database;
		this.openDatabaseCache = openDatabaseCache;
		this.alterPacketsProvider = alterPacketsProvider;
		this.sourceId = sourceId;
		this.zoneId = zoneId;


		securityPacketReceiver = new SecurityPacketReceiver(
				publicKeyLookUp,
				this::receivePacketWithIntegrity,
				(packetGroup, isFromPayloadWithIntegrity) -> {
					if (packetGroup.isTargetingNone()) {
						return true;
					}
					if (isFromPayloadWithIntegrity) {
						LOGGER.warn(SolarThingConstants.SUMMARY_MARKER, "isFromPayloadWithIntegrity=true and we are not targeting none! dateMillis: " + packetGroup.getDateMillis());
					}
					return false;
				},
				Collections.singleton(CommandOpenPacket.class),
				listenStartTime,
				fragmentId,
				sourceId,
				database.getEventDatabase()
		);
	}

	@Override
	protected void onStart() {
		super.onStart();
		listenStartTime = System.currentTimeMillis();
	}

	@Override
	protected void onUpdate() {
		super.onUpdate();
		// Supply queried solarthing_open packets to the security packet receiver, which may execute code to put commands requested to be scheduled in solarthing_alter
		List<StoredPacketGroup> packets = openDatabaseCache.getAllCachedPackets();
		securityPacketReceiver.receivePacketGroups(packets);


		// Check packets in solarthing_alter and see if we need to send a command because of a scheduled command packet
		Instant now = Instant.now();
		List<VersionedPacket<StoredAlterPacket>> alterPackets = alterPacketsProvider.getPackets();
		if (alterPackets == null) {
			LOGGER.info("alterPackets is null. Maybe query failed? Maybe additional info in previous logs?");
		} else {
			for (VersionedPacket<StoredAlterPacket> versionedPacket : alterPackets) {

				AlterPacket packet = versionedPacket.getPacket().getPacket();
				if (packet instanceof ScheduledCommandPacket) {
					ScheduledCommandPacket scheduledCommandPacket = (ScheduledCommandPacket) packet;

					ScheduledCommandData data = scheduledCommandPacket.getData();
					if (data.getScheduledTimeMillis() <= now.toEpochMilli()) {
						if (now.toEpochMilli() - data.getScheduledTimeMillis() > Duration.ofMinutes(5).toMillis()) {
							LOGGER.warn("Not going to send a command scheduled for more than 5 minutes ago! data: " + data);
						} else {
							doSendCommand(versionedPacket, scheduledCommandPacket);
						}
					}
				} else if (packet instanceof FlagPacket) {
					FlagPacket flagPacket = (FlagPacket) packet;
					FlagData data = flagPacket.getFlagData();
					ActivePeriod activePeriod = data.getActivePeriod();
					if (activePeriod instanceof TimeRangeActivePeriod) { // We only try to "manage" flags that use this type of ActivePeriod
						TimeRangeActivePeriod period = (TimeRangeActivePeriod) activePeriod;
						TimeRange timeRange = period.getTimeRange();
						Instant endTime = timeRange.getEndTime();
						if (endTime != null && endTime.compareTo(now) < 0) {
							// If there is an end time, and it is in the past, then we should remove the flag
							executorService.execute(() -> {
								try {
									database.getAlterDatabase().delete(versionedPacket);
								} catch (SolarThingDatabaseException e) {
									LOGGER.error("Could not delete a FlagPacket with an expired time", e);
									// If we cannot delete it, no need to try again, it'll still be here next time around
								}
							});
						}
					}
				}
			}
		}
	}

	@Override
	protected void onEnd(boolean peacefullyEnded) {
		super.onEnd(peacefullyEnded);
		executorService.shutdownNow();
	}

	private void receivePacketWithIntegrity(String sender, TargetPacketGroup packetGroup) {
		LOGGER.debug("Sender: " + sender + " is authorized and sent a packet targeting no fragments, so we will see if we want to handle anything from it.");
		long now = System.currentTimeMillis();
		List<StoredAlterPacket> storedAlterPacketsToUpload = new ArrayList<>();
		List<DeleteAlterPacket> deleteAlterPackets = new ArrayList<>();
		for (Packet packet : packetGroup.getPackets()) {
			if (packet instanceof ScheduleCommandPacket) {
				ScheduleCommandPacket scheduleCommandPacket = (ScheduleCommandPacket) packet;
				ScheduledCommandData data = scheduleCommandPacket.getData();
				ExecutionReason executionReason = new OpenSourceExecutionReason(new OpenSource(
						sender,
						packetGroup.getDateMillis(),
						scheduleCommandPacket,
						scheduleCommandPacket.getUniqueString() // this is legacy data and shouldn't be used anywhere, so it doesn't matter what we put here
				));
				ScheduledCommandPacket scheduledCommandPacket = new ScheduledCommandPacket(data, executionReason);
				// This databaseId is basically an arbitrary way to generate a unique ID. It contains some stuff such as the command name to debug more easily
				String databaseId = "alter-scheduled-command-" + data.getCommandName() + "-" + Long.toHexString(data.getScheduledTimeMillis()) + "-" + sender + "-" + Math.random();
				StoredAlterPacket storedAlterPacket = new ImmutableStoredAlterPacket(databaseId, now, scheduledCommandPacket, this.sourceId);
				storedAlterPacketsToUpload.add(storedAlterPacket);
			} else if (packet instanceof DeleteAlterPacket) {
				DeleteAlterPacket deleteAlterPacket = (DeleteAlterPacket) packet;
				try {
					database.validateUpdateToken(deleteAlterPacket.getUpdateToken());
					deleteAlterPackets.add(deleteAlterPacket);
				} catch (IncompatibleUpdateTokenException ex) {
					LOGGER.error(SolarThingConstants.SUMMARY_MARKER, "For some reason we have an incompatible update token!", ex);
				}
			} else if (packet instanceof RequestFlagPacket) {
				RequestFlagPacket requestFlagPacket = (RequestFlagPacket) packet;
				// Originally I was going to not upload a FlagPacket if an existing FlagPacket's
				//   time range fully encapsulated the newly requested one, but that adds some complexity
				//   that is not needed at the moment
				FlagData flagData = requestFlagPacket.getFlagData();
				ExecutionReason executionReason = new OpenSourceExecutionReason(new OpenSource(
						sender,
						packetGroup.getDateMillis(),
						requestFlagPacket,
						requestFlagPacket.getUniqueString() // this is legacy data and shouldn't be used anywhere, so it doesn't matter what we put here
				));
				FlagPacket flagPacket = new FlagPacket(flagData, executionReason);
				String databaseId = "alter-flag-" + flagData.getFlagName() + "-" + sender + "-" + Math.random();
				StoredAlterPacket storedAlterPacket = new ImmutableStoredAlterPacket(databaseId, now, flagPacket, sourceId);
				storedAlterPacketsToUpload.add(storedAlterPacket);
			} else if (packet instanceof FlagAliasAddPacket) {
				FlagAliasAddPacket flagAliasAddPacket = (FlagAliasAddPacket) packet;
				FlagAliasData flagAliasData = flagAliasAddPacket.getFlagAliasData();
				ExecutionReason executionReason = new OpenSourceExecutionReason(new OpenSource(
						sender, packetGroup.getDateMillis(), flagAliasAddPacket, flagAliasAddPacket.getUniqueString()
				));
				FlagAliasPacket flagAliasPacket = new FlagAliasPacket(flagAliasData, executionReason);
				String databaseId = "alter-flag-alias-" + flagAliasData.getFlagName() + "-" + sender + "-" + Math.random();
				StoredAlterPacket storedAlterPacket = new ImmutableStoredAlterPacket(databaseId, now, flagAliasPacket, sourceId);
				storedAlterPacketsToUpload.add(storedAlterPacket);
			}
		}
		if (storedAlterPacketsToUpload.isEmpty() && deleteAlterPackets.isEmpty()) {
			return; // Nothing for us to do, so no need to schedule a runnable
		}
		executorService.execute(() -> {
			int uploadCount = 0;
			try {
				for (StoredAlterPacket storedAlterPacket : storedAlterPacketsToUpload) {
					this.database.getAlterDatabase().upload(storedAlterPacket);
					uploadCount++;
				}
			} catch (SolarThingDatabaseException e) {
				// TODO in future we should try multiple times to upload
				LOGGER.error(SolarThingConstants.SUMMARY_MARKER, "Could not upload a stored alter packet! uploaded: " + uploadCount + " / " + storedAlterPacketsToUpload.size(), e);
			}
			int deleteCount = 0;
			try {
				for (DeleteAlterPacket deleteAlterPacket : deleteAlterPackets) {
					this.database.getAlterDatabase().delete(deleteAlterPacket.getDocumentIdToDelete(), deleteAlterPacket.getUpdateToken());
					deleteCount++;
				}
			} catch (SolarThingDatabaseException e) {
				LOGGER.error(SolarThingConstants.SUMMARY_MARKER, "Could not delete alter packets! deleted: " + deleteCount + " / " + deleteAlterPackets.size(), e);
			}
		});
	}


	// TODO put thought into how to better design the alter manager program and SolarThing database replication
	/**
	 * @deprecated This method *probably* works perfectly fine, but it is untested against malicious data and the fact that we have to use this method itself shows
	 *             how we are using this as a bandaid for the real problem: We need to be sure that the solarthing_open database is secure
	 *             from people putting in malicious packets (which can contain malicious data or have a document ID that might conflict with
	 *             the generated one that needs to be used)
	 *             This solution isn't the worst thing in the world until you start replicating solarthing_open databases to each other,
	 *             and you start running multiple alter manager programs.
	 */
	@Deprecated
	private boolean isDocumentMadeByUs(Instant now, ScheduledCommandData scheduledCommandData, StoredPacketGroup existingDocument) {
		LargeIntegrityPacket largeIntegrityPacket = (LargeIntegrityPacket) existingDocument.getPackets().stream()
				.filter(p -> p instanceof LargeIntegrityPacket)
				.findAny().orElse(null);
		if (largeIntegrityPacket == null) {
			LOGGER.warn(SolarThingConstants.SUMMARY_MARKER, "The stored document did not have a LargeIntegrity packet. Someone must be trying to stop a scheduled command!");
			return false;
		}
		String sender = largeIntegrityPacket.getSender();
		if (!commandManager.getSender().equals(sender)) {
			LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "The sender of the large integrity packet we are inspecting is not us (" + commandManager.getSender() + "). It is " + sender + ". Might be a malicious actor, might be a bad setup.");
			return false;
		}
		String encryptedHash = largeIntegrityPacket.getEncryptedHash();
		String data;
		try {
			synchronized (CIPHER) {
				data = Decrypt.decrypt(CIPHER, commandManager.getKeyPair().getPublic(), encryptedHash);
			}
		} catch (InvalidKeyException e) {
			throw new RuntimeException("Should be a valid key!", e);
		} catch (DecryptException e) {
			LOGGER.warn(SolarThingConstants.SUMMARY_MARKER, "The document we are inspecting had a large integrity packet with the sender: " + sender + ", but that's us and we couldn't decrypt their payload. Likely a malicious actor", e);
			return false;
		}
		final String[] split = data.split(",", 2);
		LOGGER.debug("decrypted data: " + data);
		if (split.length != 2) {
			LOGGER.warn(SolarThingConstants.SUMMARY_MARKER, "split.length: " + split.length + " split: " + Arrays.asList(split));
			return false;
		}
		String hexMillis = split[0];
		//String message = split[1]; We don't care what the message is. We don't even care enough to check if it matches the payload's hash
		long dateMillis;
		try {
			dateMillis = Long.parseLong(hexMillis, 16);
		} catch (NumberFormatException e) {
			LOGGER.error(SolarThingConstants.SUMMARY_MARKER, "Error parsing hex date millis", e);
			return false;
		}
		if (dateMillis < scheduledCommandData.getScheduledTimeMillis()) {
			LOGGER.warn(SolarThingConstants.SUMMARY_MARKER, "The dateMillis for this is less than the command's scheduled execution time! This must be a malicious actor!");
			return false;
		}
		if (dateMillis > now.toEpochMilli()) {
			LOGGER.warn(SolarThingConstants.SUMMARY_MARKER, "The dateMillis for this is greater than now! This should never ever happen.");
			return false;
		}
		return true;
	}

	private void doSendCommand(VersionedPacket<StoredAlterPacket> versionedPacket, ScheduledCommandPacket scheduledCommandPacket) {
		LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Sending command from data: " + scheduledCommandPacket.getData());
		ScheduledCommandData data = scheduledCommandPacket.getData();
		RequestCommandPacket requestCommandPacket = new ImmutableRequestCommandPacket(data.getCommandName());
		// Having a document ID based off of the StoredAlterPacket's _id helps make sure we don't process it twice in case we are unable to delete it.
		//   -- if there's an update conflict while uploading, we know we already processed it
		String documentId = "scheduled-request-" + versionedPacket.getPacket().getDbId();
		PacketCollectionCreator creator = commandManager.makeCreator(sourceId, zoneId, InstanceTargetPackets.create(data.getTargetFragmentIds()), requestCommandPacket, zonedDateTime -> documentId);
		executorService.execute(() -> {
			Instant uploadingNow = Instant.now();
			PacketCollection packetCollection = creator.create(uploadingNow);
			boolean shouldDeleteAlter = false;
			try {
				database.getOpenDatabase().uploadPacketCollection(packetCollection, null);
				LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Successfully uploaded packet collection that schedules command from data: " + scheduledCommandPacket.getData() + " document ID: " + documentId);
				shouldDeleteAlter = true;
			} catch (UpdateConflictSolarThingDatabaseException e) {
				LOGGER.error("Got update conflict exception while uploading document ID: " + documentId + ". Will inspect existing document and overwrite if it's a malicious actor...", e);
				VersionedPacket<StoredPacketGroup> existingDocument = null;
				try {
					existingDocument = database.getOpenDatabase().getPacketCollection(documentId);
				} catch (SolarThingDatabaseException ex) {
					LOGGER.error("Could not retrieve document with document ID: " + documentId, ex);
				}
				if (existingDocument != null) {
					if (isDocumentMadeByUs(uploadingNow, data, existingDocument.getPacket())) {
						LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "False alarm everyone. The packet in the database was made by us and its timestamp is reasonable. document ID: " + documentId);
						shouldDeleteAlter = true;
					} else {
						LOGGER.warn(SolarThingConstants.SUMMARY_MARKER, "The packet in the database with document ID: " + documentId + " was not made by us. Could be a malicious actor. We will overwrite that packet.");
						try {
							database.getOpenDatabase().uploadPacketCollection(packetCollection, existingDocument.getUpdateToken());
							shouldDeleteAlter = true;
						} catch (SolarThingDatabaseException ex) {
							LOGGER.error("Could not overwrite malicious packet. Will likely try again. document ID: " + documentId, ex);
						}
					}
				}
			} catch (SolarThingDatabaseException e) {
				LOGGER.error("Failed to upload our request command packet. documentId: " + documentId, e);
			}
			if (shouldDeleteAlter) {
				try {
					database.getAlterDatabase().delete(versionedPacket);
				} catch (SolarThingDatabaseException e) {
					LOGGER.error("Error while deleting an alter document. document ID: " + versionedPacket.getPacket().getDbId() + " update token: " + versionedPacket.getUpdateToken(), e);
				}
			}
		});
	}

}

package me.retrodaredevil.solarthing.actions.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.action.node.environment.InjectEnvironment;
import me.retrodaredevil.solarthing.AlterPacketsProvider;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.action.node.environment.*;
import me.retrodaredevil.solarthing.commands.packets.open.CommandOpenPacket;
import me.retrodaredevil.solarthing.commands.packets.open.ImmutableRequestCommandPacket;
import me.retrodaredevil.solarthing.commands.packets.open.RequestCommandPacket;
import me.retrodaredevil.solarthing.commands.packets.open.ScheduleCommandPacket;
import me.retrodaredevil.solarthing.database.SolarThingDatabase;
import me.retrodaredevil.solarthing.database.VersionedPacket;
import me.retrodaredevil.solarthing.database.cache.DatabaseCache;
import me.retrodaredevil.solarthing.database.exception.SolarThingDatabaseException;
import me.retrodaredevil.solarthing.database.exception.UpdateConflictSolarThingDatabaseException;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.collection.StoredPacketGroup;
import me.retrodaredevil.solarthing.packets.instance.InstanceTargetPackets;
import me.retrodaredevil.solarthing.packets.security.LargeIntegrityPacket;
import me.retrodaredevil.solarthing.packets.security.crypto.Decrypt;
import me.retrodaredevil.solarthing.packets.security.crypto.DecryptException;
import me.retrodaredevil.solarthing.packets.security.crypto.InvalidKeyException;
import me.retrodaredevil.solarthing.packets.security.crypto.KeyUtil;
import me.retrodaredevil.solarthing.program.SecurityPacketReceiver;
import me.retrodaredevil.solarthing.reason.ExecutionReason;
import me.retrodaredevil.solarthing.reason.OpenSourceExecutionReason;
import me.retrodaredevil.solarthing.type.alter.AlterPacket;
import me.retrodaredevil.solarthing.type.alter.ImmutableStoredAlterPacket;
import me.retrodaredevil.solarthing.type.alter.StoredAlterPacket;
import me.retrodaredevil.solarthing.type.alter.packets.ScheduledCommandData;
import me.retrodaredevil.solarthing.type.alter.packets.ScheduledCommandPacket;
import me.retrodaredevil.solarthing.type.open.OpenSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@JsonTypeName("alter_manager")
public class AlterManagerActionNode implements ActionNode {
	private static final Logger LOGGER = LoggerFactory.getLogger(AlterManagerActionNode.class);
	private static final Cipher CIPHER;

	static {
		try {
			CIPHER = Cipher.getInstance(KeyUtil.CIPHER_TRANSFORMATION);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			throw new RuntimeException(e);
		}
	}

	private final ExecutorService executorService = Executors.newSingleThreadExecutor();
	private final SecurityPacketReceiver.State state = new SecurityPacketReceiver.State();
	private final long listenStartTime = System.currentTimeMillis();

	private final String sender;
	private final CommandManager commandManager;

	public AlterManagerActionNode(
			@JsonProperty(value = "sender", required = true) String sender,
			@JsonProperty(value = "key_directory", required = true) File keyDirectory
	) {
		this.sender = sender;
		commandManager = new CommandManager(keyDirectory, sender);
	}

	private boolean isDocumentMadeByUs(Instant now, ScheduledCommandData scheduledCommandData, StoredPacketGroup existingDocument) {
		LargeIntegrityPacket largeIntegrityPacket = (LargeIntegrityPacket) existingDocument.getPackets().stream()
				.filter(p -> p instanceof LargeIntegrityPacket)
				.findAny().orElse(null);
		if (largeIntegrityPacket == null) {
			LOGGER.warn(SolarThingConstants.SUMMARY_MARKER, "The stored document did not have a LargeIntegrity packet. Someone must be trying to stop a scheduled command!");
			return false;
		}
		String sender = largeIntegrityPacket.getSender();
		if (!this.sender.equals(sender)) {
			LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "The sender of the large integrity packet we are inspecting is not us (" + this.sender + "). It is " + sender + ". Might be a malicious actor, might be a bad setup.");
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

	private void doSendCommand(InjectEnvironment injectEnvironment, SolarThingDatabase database, VersionedPacket<StoredAlterPacket> versionedPacket, ScheduledCommandPacket scheduledCommandPacket) {
		LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Sending command from data: " + scheduledCommandPacket.getData());
		ScheduledCommandData data = scheduledCommandPacket.getData();
		RequestCommandPacket requestCommandPacket = new ImmutableRequestCommandPacket(data.getCommandName());
		// Having a document ID based off of the StoredAlterPacket's _id helps make sure we don't process it twice in case we are unable to delete it.
		//   -- if there's an update conflict while uploading, we know we already processed it
		String documentId = "scheduled-request-" + versionedPacket.getPacket().getDbId();
		CommandManager.Creator creator = commandManager.makeCreator(injectEnvironment, InstanceTargetPackets.create(data.getTargetFragmentIds()), requestCommandPacket, zonedDateTime -> documentId);
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

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		SolarThingDatabaseEnvironment solarThingDatabaseEnvironment = actionEnvironment.getInjectEnvironment().get(SolarThingDatabaseEnvironment.class);
		OpenDatabaseCacheEnvironment openDatabaseCacheEnvironment = actionEnvironment.getInjectEnvironment().get(OpenDatabaseCacheEnvironment.class);
		AuthorizationEnvironment authorizationEnvironment = actionEnvironment.getInjectEnvironment().get(AuthorizationEnvironment.class);
		AlterPacketsEnvironment alterPacketsEnvironment = actionEnvironment.getInjectEnvironment().get(AlterPacketsEnvironment.class);
		SourceIdEnvironment sourceIdEnvironment = actionEnvironment.getInjectEnvironment().get(SourceIdEnvironment.class);

		SolarThingDatabase database = solarThingDatabaseEnvironment.getSolarThingDatabase();
		DatabaseCache openDatabaseCache = openDatabaseCacheEnvironment.getOpenDatabaseCache();
		String sourceId = sourceIdEnvironment.getSourceId();
		SecurityPacketReceiver securityPacketReceiver = new SecurityPacketReceiver(
				authorizationEnvironment.getPublicKeyLookUp(),
				(sender, packetGroup) -> {
					LOGGER.debug("Sender: " + sender + " is authorized and sent a packet targeting no fragments, so we will see if we want to handle anything from it.");
					long now = System.currentTimeMillis();
					List<StoredAlterPacket> storedAlterPacketsToUpload = new ArrayList<>();
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
							// TODO is this how we want to generate the ID?
							String databaseId = "alter-scheduled-command-" + data.getCommandName() + "-" + Long.toHexString(data.getScheduledTimeMillis()) + "-" + sender + "-" + Math.random();
							StoredAlterPacket storedAlterPacket = new ImmutableStoredAlterPacket(databaseId, now, scheduledCommandPacket, sourceId);
							storedAlterPacketsToUpload.add(storedAlterPacket);
						}
					}
					executorService.execute(() -> {
						int uploadCount = 0;
						try {
							for (StoredAlterPacket storedAlterPacket : storedAlterPacketsToUpload) {
								database.getAlterDatabase().upload(storedAlterPacket);
								uploadCount++;
							}
						} catch (SolarThingDatabaseException e) {
							// TODO in future we should try multiple times to upload
							LOGGER.error(SolarThingConstants.SUMMARY_MARKER, "Could not upload a stored alter packet! uploaded: " + uploadCount + " / " + storedAlterPacketsToUpload.size(), e);
						}
					});
				},
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
				state
		);

		AlterPacketsProvider alterPacketsProvider = alterPacketsEnvironment.getAlterPacketsProvider();

		// This action is designed to be used in the automation program, which will create a new action each iteration. That's why this only runs once
		return Actions.createRunOnce(() -> {
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
								doSendCommand(actionEnvironment.getInjectEnvironment(), database, versionedPacket, scheduledCommandPacket);
							}
						}
					}
				}
			}
		});
	}
}

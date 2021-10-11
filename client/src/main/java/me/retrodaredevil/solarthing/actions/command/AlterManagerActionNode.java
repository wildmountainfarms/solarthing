package me.retrodaredevil.solarthing.actions.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.solarthing.AlterPacketsProvider;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.actions.ActionNode;
import me.retrodaredevil.solarthing.actions.environment.*;
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

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@JsonTypeName("alter_manager")
public class AlterManagerActionNode implements ActionNode {
	private static final Logger LOGGER = LoggerFactory.getLogger(AlterManagerActionNode.class);

	private final ExecutorService executorService = Executors.newSingleThreadExecutor();
	private final SecurityPacketReceiver.State state = new SecurityPacketReceiver.State();
	private final long listenStartTime = System.currentTimeMillis();

	private final CommandManager commandManager;

	public AlterManagerActionNode(
			@JsonProperty(value = "sender", required = true) String sender,
			@JsonProperty(value = "key_directory", required = true) File keyDirectory
	) {
		commandManager = new CommandManager(keyDirectory, sender);
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
						LOGGER.warn("isFromPayloadWithIntegrity=true and we are not targeting none! dateMillis: " + packetGroup.getDateMillis());
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
							RequestCommandPacket requestCommandPacket = new ImmutableRequestCommandPacket(data.getCommandName());
							// Having a document ID based off of the StoredAlterPacket's _id helps make sure we don't process it twice in case we are unable to delete it.
							//   -- if there's an update conflict while uploading, we know we already processed it
							String documentId = "scheduled-request-" + versionedPacket.getPacket().getDbId();
							CommandManager.Creator creator = commandManager.makeCreator(actionEnvironment.getInjectEnvironment(), InstanceTargetPackets.create(data.getTargetFragmentIds()), requestCommandPacket, zonedDateTime -> documentId);
							executorService.execute(() -> {
								// The time that is stored in the packet collection should be as close as possible to the actual. Since we are in a different thread
								//   and don't really know how long this Runnable has been waiting to run, we call Instant.now() to get a more reliable time.
								PacketCollection packetCollection = creator.create(Instant.now());
								boolean shouldDeleteAlter = true;
								try {
									database.getOpenDatabase().uploadPacketCollection(packetCollection, null);
								} catch (UpdateConflictSolarThingDatabaseException e) {
									// TODO SERIOUSLY, ACTUALLY GET TO THIS: The solarthing_open database can be modified by anyone. Almost all packets in it
									//   are encrypted for integrity. As of now, the code assumes that an update conflict exception is the result of us already handling
									//   a given alter packet and us being unable to delete it for whatever reason. This is a reasonable assumption, but since the data
									//   in solarthing_alter is public, someone could easily figure out what the document ID will be for some given scheduled command,
									//   then upload *any* document under that document ID. This makes us assume that we have processed a given scheduled command,
									//   when we actually haven't.
									//   --Someone basically just cancelled a scheduled command without any authorization whatsoever.
									LOGGER.debug("We already uploaded packet with documentId: " + documentId + ". We will assume there is no malicious actor preventing this packet from uploading and delete the alter packet so no further processing is attempted..", e);
								} catch (SolarThingDatabaseException e) {
									LOGGER.error("Failed to upload our request command packet. documentId: " + documentId, e);
									shouldDeleteAlter = false;
								}
								if (shouldDeleteAlter) {
									try {
										database.getAlterDatabase().delete(versionedPacket);
									} catch (SolarThingDatabaseException e) {
										LOGGER.error("Error while deleting an alter document", e);
									}
								}
							});
						}
					}
				}
			}
		});
	}
}

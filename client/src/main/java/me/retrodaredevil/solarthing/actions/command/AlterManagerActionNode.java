package me.retrodaredevil.solarthing.actions.command;

import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.actions.ActionNode;
import me.retrodaredevil.solarthing.actions.environment.*;
import me.retrodaredevil.solarthing.commands.packets.open.CommandOpenPacket;
import me.retrodaredevil.solarthing.commands.packets.open.ScheduleCommandPacket;
import me.retrodaredevil.solarthing.database.SolarThingDatabase;
import me.retrodaredevil.solarthing.database.cache.DatabaseCache;
import me.retrodaredevil.solarthing.database.exception.SolarThingDatabaseException;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.StoredPacketGroup;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@JsonTypeName("alter_manager")
public class AlterManagerActionNode implements ActionNode {
	private static final Logger LOGGER = LoggerFactory.getLogger(AlterManagerActionNode.class);

	private final ExecutorService executorService = Executors.newSingleThreadExecutor();

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		SolarThingDatabaseEnvironment solarThingDatabaseEnvironment = actionEnvironment.getInjectEnvironment().get(SolarThingDatabaseEnvironment.class);
		OpenDatabaseCacheEnvironment openDatabaseCacheEnvironment = actionEnvironment.getInjectEnvironment().get(OpenDatabaseCacheEnvironment.class);
		AuthorizationEnvironment authorizationEnvironment = actionEnvironment.getInjectEnvironment().get(AuthorizationEnvironment.class);
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
				Collections.singleton(CommandOpenPacket.class)
		);

		// This action is designed to be used in the automation program, which will create a new action each iteration. That's why this only runs once
		return Actions.createRunOnce(() -> {
			List<StoredPacketGroup> packets = openDatabaseCache.getAllCachedPackets();
			securityPacketReceiver.receivePacketGroups(packets);

			// TODO look at the packets in the alter database and do stuff based on them
		});
	}
}

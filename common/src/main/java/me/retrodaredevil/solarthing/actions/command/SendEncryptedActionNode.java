package me.retrodaredevil.solarthing.actions.command;

import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.solarthing.actions.environment.SolarThingDatabaseEnvironment;
import me.retrodaredevil.solarthing.actions.environment.SourceIdEnvironment;
import me.retrodaredevil.solarthing.actions.environment.TimeZoneEnvironment;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.commands.packets.open.CommandOpenPacket;
import me.retrodaredevil.solarthing.commands.util.CommandManager;
import me.retrodaredevil.solarthing.database.SolarThingDatabase;
import me.retrodaredevil.solarthing.database.exception.SolarThingDatabaseException;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.collection.PacketCollectionIdGenerator;
import me.retrodaredevil.solarthing.packets.instance.InstanceTargetPackets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

class SendEncryptedActionNode implements ActionNode {
	private static final Logger LOGGER = LoggerFactory.getLogger(SendEncryptedActionNode.class);

	private final CommandManager commandManager;
	private final List<Integer> fragmentIdTargets;
	private final Supplier<@NotNull CommandOpenPacket> packetSupplier;

	private final ExecutorService executorService = Executors.newSingleThreadExecutor();

	public SendEncryptedActionNode(
			CommandManager commandManager,
			List<Integer> fragmentIdTargets,
			Supplier<@NotNull CommandOpenPacket> packetSupplier) {
		requireNonNull(this.commandManager = commandManager);
		requireNonNull(this.fragmentIdTargets = fragmentIdTargets);
		requireNonNull(this.packetSupplier = packetSupplier);
	}


	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		SolarThingDatabase database = actionEnvironment.getInjectEnvironment().get(SolarThingDatabaseEnvironment.class).getSolarThingDatabase();
		String sourceId = actionEnvironment.getInjectEnvironment().get(SourceIdEnvironment.class).getSourceId();
		ZoneId zoneId = actionEnvironment.getInjectEnvironment().get(TimeZoneEnvironment.class).getZoneId();
		CommandOpenPacket packet = packetSupplier.get();
		requireNonNull(packet, "The supplier should have given us a packet!");
		CommandManager.Creator creator = commandManager.makeCreator(
				sourceId,
				zoneId,
				InstanceTargetPackets.create(fragmentIdTargets),
				packet,
				PacketCollectionIdGenerator.Defaults.UNIQUE_GENERATOR
		);
		return Actions.createRunOnce(() -> {
			Instant now = Instant.now();
			PacketCollection packetCollection = creator.create(now);
			executorService.execute(() -> {
				try {
					database.getOpenDatabase().uploadPacketCollection(packetCollection, null);
					LOGGER.info("Uploaded command request document");
				} catch (SolarThingDatabaseException e) {
					LOGGER.error("Error while uploading document.", e);
				}
			});
		});
	}
}

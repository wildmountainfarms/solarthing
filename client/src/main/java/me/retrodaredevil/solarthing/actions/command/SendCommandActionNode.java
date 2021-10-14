package me.retrodaredevil.solarthing.actions.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.action.node.environment.SolarThingDatabaseEnvironment;
import me.retrodaredevil.solarthing.commands.packets.open.ImmutableRequestCommandPacket;
import me.retrodaredevil.solarthing.commands.packets.open.RequestCommandPacket;
import me.retrodaredevil.solarthing.database.SolarThingDatabase;
import me.retrodaredevil.solarthing.database.exception.SolarThingDatabaseException;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.collection.PacketCollectionIdGenerator;
import me.retrodaredevil.solarthing.packets.instance.InstanceTargetPackets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.Objects.requireNonNull;

@JsonTypeName("sendcommand")
public class SendCommandActionNode implements ActionNode {
	private static final Logger LOGGER = LoggerFactory.getLogger(SendCommandActionNode.class);

	private final CommandManager commandManager;
	private final List<Integer> fragmentIdTargets;
	private final RequestCommandPacket requestCommandPacket;

	private final ExecutorService executorService = Executors.newSingleThreadExecutor();

	@JsonCreator
	public SendCommandActionNode(
			@JsonProperty(value = "directory", required = true) File keyDirectory,
			@JsonProperty(value = "targets", required = true) List<Integer> fragmentIdTargets,
			@JsonProperty(value = "command", required = true) String commandName,
			@JsonProperty(value = "sender", required = true) String sender) {
		commandManager = new CommandManager(keyDirectory, sender);
		requireNonNull(this.fragmentIdTargets = fragmentIdTargets);
		requestCommandPacket = new ImmutableRequestCommandPacket(commandName);
	}


	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		SolarThingDatabase database = actionEnvironment.getInjectEnvironment().get(SolarThingDatabaseEnvironment.class).getSolarThingDatabase();
		CommandManager.Creator creator = commandManager.makeCreator(
				actionEnvironment.getInjectEnvironment(),
				InstanceTargetPackets.create(fragmentIdTargets),
				requestCommandPacket,
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

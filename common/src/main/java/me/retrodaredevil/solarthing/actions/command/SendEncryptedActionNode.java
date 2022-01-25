package me.retrodaredevil.solarthing.actions.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.action.WhenDone;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.action.node.PassActionNode;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.solarthing.actions.command.provider.CommandOpenProvider;
import me.retrodaredevil.solarthing.actions.environment.SolarThingDatabaseEnvironment;
import me.retrodaredevil.solarthing.actions.environment.SourceIdEnvironment;
import me.retrodaredevil.solarthing.actions.environment.TimeZoneEnvironment;
import me.retrodaredevil.solarthing.commands.packets.open.CommandOpenPacket;
import me.retrodaredevil.solarthing.commands.util.CommandManager;
import me.retrodaredevil.solarthing.database.SolarThingDatabase;
import me.retrodaredevil.solarthing.packets.collection.PacketCollectionCreator;
import me.retrodaredevil.solarthing.packets.collection.PacketCollectionIdGenerator;
import me.retrodaredevil.solarthing.packets.instance.InstanceTargetPackets;

import java.io.File;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import static java.util.Objects.requireNonNull;

/**
 * An action node that is designed to create an action that uploads an encrypted packet to the open database.
 * This class will handle the encrypting of the packet.
 */
@JsonTypeName("sendopen")
public class SendEncryptedActionNode implements ActionNode {

	private final CommandManager commandManager;
	private final List<Integer> fragmentIdTargets;
	private final CommandOpenProvider packetProvider;

	private final ThreadFactory threadFactory = Executors.defaultThreadFactory();

	public SendEncryptedActionNode(
			CommandManager commandManager,
			List<Integer> fragmentIdTargets,
			CommandOpenProvider packetProvider) {
		requireNonNull(this.commandManager = commandManager);
		requireNonNull(this.fragmentIdTargets = fragmentIdTargets);
		requireNonNull(this.packetProvider = packetProvider);
	}

	@JsonCreator
	public static SendEncryptedActionNode create(
			@JsonProperty(value = "directory", required = true) File keyDirectory,
			@JsonProperty(value = "sender", required = true) String sender,
			@JsonProperty(value = "targets", required = true) List<Integer> fragmentIdTargets,
			@JsonProperty(value = "data", required = true) CommandOpenProvider data
	) {
		return new SendEncryptedActionNode(
				new CommandManager(keyDirectory, sender), fragmentIdTargets,
				data
		);
	}


	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		SolarThingDatabase database = actionEnvironment.getInjectEnvironment().get(SolarThingDatabaseEnvironment.class).getSolarThingDatabase();
		String sourceId = actionEnvironment.getInjectEnvironment().get(SourceIdEnvironment.class).getSourceId();
		ZoneId zoneId = actionEnvironment.getInjectEnvironment().get(TimeZoneEnvironment.class).getZoneId();
		CommandOpenPacket packet = packetProvider.get();
		requireNonNull(packet, "The supplier should have given us a packet!");
		PacketCollectionCreator creator = commandManager.makeCreator(
				sourceId,
				zoneId,
				InstanceTargetPackets.create(fragmentIdTargets),
				packet,
				PacketCollectionIdGenerator.Defaults.UNIQUE_GENERATOR
		);
		return Actions.createLinkedActionRunner(
				new SendPacketAction(
						threadFactory,
						database::getOpenDatabase,
						creator, 100, 10,
						PassActionNode.getInstance().createAction(actionEnvironment), // TODO allow actions to be passed in for these
						PassActionNode.getInstance().createAction(actionEnvironment)
				),
				WhenDone.BE_DONE,
				true
		);
	}
}

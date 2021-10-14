package me.retrodaredevil.solarthing.program;

import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.ActionMultiplexer;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.solarthing.type.open.OpenSource;
import me.retrodaredevil.solarthing.PacketGroupReceiver;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.solarthing.actions.command.EnvironmentUpdater;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.action.node.environment.InjectEnvironment;
import me.retrodaredevil.action.node.environment.VariableEnvironment;
import me.retrodaredevil.solarthing.commands.packets.open.CommandOpenPacket;
import me.retrodaredevil.solarthing.commands.packets.open.CommandOpenPacketType;
import me.retrodaredevil.solarthing.commands.packets.open.RequestCommandPacket;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.TargetPacketGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ActionNodeDataReceiver implements PacketGroupReceiver {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionNodeDataReceiver.class);
	/*
	Note: This class has comments about making sure things are thread safe because PacketGroupReceivers are almost always called from another thread
	*/

	private final VariableEnvironment variableEnvironment = new VariableEnvironment();
	private final ActionMultiplexer actionMultiplexer = new Actions.ActionMultiplexerBuilder().build(); // action-lib is designed to be thread safe

	private final Map<String, ActionNode> actionNodeMap; // action nodes are not used directly, so thread safety is fine here. (Unless an ActionNode has weird state and isn't thread safe)
	private final EnvironmentUpdater environmentUpdater; // supplied by the caller, assumed to be thread safe.

	/**
	 * @param environmentUpdater Called when a command has been requested and recognized. Note that this is called in a separate thread , so if you are
	 *                           for some reason accessing mutating state, make sure your access is thread safe.
	 */
	public ActionNodeDataReceiver(Map<String, ActionNode> actionNodeMap, EnvironmentUpdater environmentUpdater) {
		this.actionNodeMap = actionNodeMap;
		this.environmentUpdater = environmentUpdater;
	}

	public Action getActionUpdater() {
		return actionMultiplexer;
	}

	@Override
	public void receivePacketGroup(String sender, TargetPacketGroup packetGroup) {
		for (Packet packet : packetGroup.getPackets()) {
			if (packet instanceof CommandOpenPacket) {
				CommandOpenPacket commandOpenPacket = (CommandOpenPacket) packet;
				if (commandOpenPacket.getPacketType() == CommandOpenPacketType.REQUEST_COMMAND) {
					RequestCommandPacket requestCommand = (RequestCommandPacket) commandOpenPacket;

					OpenSource source = new OpenSource(sender, packetGroup.getDateMillis(), requestCommand, requestCommand.getCommandName());
					receiveData(source, requestCommand.getCommandName());
				}
			}
		}
	}

	private void receiveData(OpenSource source, String commandName) {
		ActionNode requested = actionNodeMap.get(commandName);
		if(requested != null){
			InjectEnvironment.Builder injectEnvironmentBuilder = new InjectEnvironment.Builder();
			environmentUpdater.updateInjectEnvironment(source, injectEnvironmentBuilder);
			Action action = requested.createAction(new ActionEnvironment(variableEnvironment, new VariableEnvironment(), injectEnvironmentBuilder.build()));
			// Now that action has been created, add it to the action multiplexer. (Adding is thread safe).
			//   The action has not been used by this thread, so when a different thread starts executing it, there will be no problems.
			actionMultiplexer.add(action);
			LOGGER.info(SolarThingConstants.SUMMARY_MARKER, source.getSender() + " has requested command sequence: " + commandName);
		} else {
			LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Sender: " + source.getSender() + " has requested unknown command: " + commandName);
		}
	}

}

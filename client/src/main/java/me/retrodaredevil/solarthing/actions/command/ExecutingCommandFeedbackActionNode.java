package me.retrodaredevil.solarthing.actions.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.solarthing.InstantType;
import me.retrodaredevil.solarthing.actions.ActionNode;
import me.retrodaredevil.solarthing.actions.environment.ActionEnvironment;
import me.retrodaredevil.solarthing.actions.environment.EventReceiverEnvironment;
import me.retrodaredevil.solarthing.actions.environment.SourceEnvironment;
import me.retrodaredevil.solarthing.open.OpenSource;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.program.PacketListReceiverHandler;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

@JsonTypeName("feedback")
public class ExecutingCommandFeedbackActionNode implements ActionNode {
	private final String message;
	private final String category;

	@JsonCreator
	public ExecutingCommandFeedbackActionNode(
			@JsonProperty(value = "message", required = true) String message,
			@JsonProperty(value = "category", required = true) String category) {
		requireNonNull(this.message = message);
		requireNonNull(this.category = category);
	}
	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		SourceEnvironment sourceEnvironment = actionEnvironment.getInjectEnvironment().get(SourceEnvironment.class);
		EventReceiverEnvironment eventReceiverEnvironment = actionEnvironment.getInjectEnvironment().get(EventReceiverEnvironment.class);
		OpenSource source = sourceEnvironment.getSource();
		PacketListReceiverHandler packetListReceiverHandler = eventReceiverEnvironment.getEventPacketListReceiverHandler();

		List<Packet> packets = new ArrayList<>();
		return Actions.createRunOnce(() -> {
			packetListReceiverHandler.getPacketListReceiverAccepter().receive(packets, InstantType.INSTANT);
			packetListReceiverHandler.pack(InstantType.INSTANT);
			packetListReceiverHandler.handle(InstantType.INSTANT);
		});
	}
}

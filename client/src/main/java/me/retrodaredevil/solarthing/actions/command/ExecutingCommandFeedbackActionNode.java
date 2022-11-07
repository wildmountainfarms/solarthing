package me.retrodaredevil.solarthing.actions.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.solarthing.actions.environment.EventReceiverEnvironment;
import me.retrodaredevil.solarthing.actions.environment.ExecutionReasonEnvironment;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.program.PacketListReceiverHandler;
import me.retrodaredevil.solarthing.reason.ExecutionReason;
import me.retrodaredevil.solarthing.type.event.feedback.ImmutableExecutionFeedbackPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static java.util.Objects.requireNonNull;

@JsonTypeName("feedback")
public class ExecutingCommandFeedbackActionNode implements ActionNode {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExecutingCommandFeedbackActionNode.class);

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
		ExecutionReasonEnvironment  executionReasonEnvironment = actionEnvironment.getInjectEnvironment().get(ExecutionReasonEnvironment.class);
		EventReceiverEnvironment eventReceiverEnvironment = actionEnvironment.getInjectEnvironment().get(EventReceiverEnvironment.class);

		ExecutionReason executionReason = executionReasonEnvironment.getExecutionReason();
		PacketListReceiverHandler packetListReceiverHandler = eventReceiverEnvironment.getEventPacketListReceiverHandler();

		List<Packet> packets = Arrays.asList(new ImmutableExecutionFeedbackPacket(message, category, executionReason));
		return Actions.createRunOnce(() -> {
			LOGGER.debug("Going to upload an execution feedback packet.");
			Instant now = Instant.now();
			packetListReceiverHandler.uploadSimple(now, packets);
		});
	}
}

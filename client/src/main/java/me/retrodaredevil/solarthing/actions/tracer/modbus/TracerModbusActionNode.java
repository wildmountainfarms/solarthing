package me.retrodaredevil.solarthing.actions.tracer.modbus;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.action.WhenDone;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.action.node.PassActionNode;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.solarthing.actions.environment.MultiTracerModbusEnvironment;
import me.retrodaredevil.solarthing.actions.environment.TracerErrorEnvironment;
import me.retrodaredevil.solarthing.actions.environment.TracerModbusEnvironment;
import me.retrodaredevil.solarthing.actions.error.TryErrorStateAction;
import me.retrodaredevil.solarthing.packets.identification.NumberedIdentifier;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * @see me.retrodaredevil.solarthing.actions.rover.modbus.RoverModbusActionNode This class is copy pasted and is the tracer version of this.
 */
@JsonTypeName("tracer-modbus")
public class TracerModbusActionNode implements ActionNode {
	private final int number;
	private final List<ActionNode> sequentialActions;
	private final ActionNode successAction;
	private final ActionNode errorAction;

	@JsonCreator
	public TracerModbusActionNode(
			@JsonProperty("number") Integer number,
			@JsonProperty(value = "actions", required = true) List<ActionNode> sequentialActions,
			@JsonProperty(value = "on_success") ActionNode successAction,
			@JsonProperty(value = "on_error") ActionNode errorAction
	) {
		this.number = number == null ? NumberedIdentifier.DEFAULT_NUMBER : number;
		requireNonNull(this.sequentialActions = sequentialActions);
		this.successAction = successAction == null ? PassActionNode.getInstance() : successAction;
		this.errorAction = errorAction == null ? PassActionNode.getInstance() : errorAction;
	}

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		MultiTracerModbusEnvironment multiTracerModbusEnvironment = actionEnvironment.getInjectEnvironment().get(MultiTracerModbusEnvironment.class);
		TracerModbusEnvironment tracerModbusEnvironment = multiTracerModbusEnvironment.getOrNull(number);
		if (tracerModbusEnvironment == null) {
			throw new IllegalStateException("No tracer modbus environment for number: " + number);
		}
		Action success = successAction.createAction(actionEnvironment);
		Action error = errorAction.createAction(actionEnvironment);

		TracerErrorEnvironment tracerErrorEnvironment = new TracerErrorEnvironment();

		ActionEnvironment injectedActionEnvironment = new ActionEnvironment(
				actionEnvironment.getGlobalEnvironment(),
				actionEnvironment.getLocalEnvironment(),
				actionEnvironment.getInjectEnvironment().newBuilder()
						.add(tracerModbusEnvironment)
						.add(tracerErrorEnvironment)
						.build()
		);

		List<Action> actions = sequentialActions.stream().map(actionNode -> actionNode.createAction(injectedActionEnvironment)).collect(Collectors.toList());

		return Actions.createLinkedActionRunner(new TryErrorStateAction(actions, success, error, tracerErrorEnvironment.getTracerActionErrorState()), WhenDone.BE_DONE, true);
	}
}

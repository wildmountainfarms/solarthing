package me.retrodaredevil.solarthing.actions.rover.modbus;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.action.LinkedAction;
import me.retrodaredevil.action.SimpleAction;
import me.retrodaredevil.action.WhenDone;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.action.node.PassActionNode;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.solarthing.actions.environment.MultiRoverModbusEnvironment;
import me.retrodaredevil.solarthing.actions.environment.RoverErrorEnvironment;
import me.retrodaredevil.solarthing.actions.environment.RoverModbusEnvironment;
import me.retrodaredevil.solarthing.actions.rover.RoverActionErrorState;
import me.retrodaredevil.solarthing.packets.identification.NumberedIdentifier;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * Injects a {@link RoverModbusEnvironment} into (multiple) actions.
 * Each given action will be executed sequentially, only executing the next action if the previous action was successful.
 * After either all actions have been executed or an error has occurred in any of the actions, the "on_success" or "on_error"
 * action will be run.
 * <p>
 * The success and error actions do not have a {@link RoverModbusEnvironment} injected into them.
 * If an error occurs while an action is executing, the action will complete then the "on_error" action will become active.
 */
@JsonTypeName("rover-modbus")
public class RoverModbusActionNode implements ActionNode {
	private final int number;
	private final List<ActionNode> sequentialActions;
	private final ActionNode successAction;
	private final ActionNode errorAction;

	@JsonCreator
	public RoverModbusActionNode(
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
		MultiRoverModbusEnvironment multiRoverModbusEnvironment = actionEnvironment.getInjectEnvironment().get(MultiRoverModbusEnvironment.class);
		RoverModbusEnvironment roverModbusEnvironment = multiRoverModbusEnvironment.getOrNull(number);
		if (roverModbusEnvironment == null) {
			throw new IllegalStateException("No rover modbus environment for number: " + number);
		}
		Action success = successAction.createAction(actionEnvironment);
		Action error = errorAction.createAction(actionEnvironment);

		RoverErrorEnvironment roverErrorEnvironment = new RoverErrorEnvironment();

		ActionEnvironment injectedActionEnvironment = new ActionEnvironment(
				actionEnvironment.getGlobalEnvironment(),
				actionEnvironment.getLocalEnvironment(),
				actionEnvironment.getInjectEnvironment().newBuilder()
						.add(roverModbusEnvironment)
						.add(roverErrorEnvironment)
						.build()
		);

		List<Action> actions = sequentialActions.stream().map(actionNode -> actionNode.createAction(injectedActionEnvironment)).collect(Collectors.toList());

		return Actions.createLinkedActionRunner(new RoverModbusAction(actions, success, error, roverErrorEnvironment.getRoverActionErrorState()), WhenDone.BE_DONE, true);
	}

	private class RoverModbusAction extends SimpleAction implements LinkedAction {

		private final List<Action> actions;
		private final Action successAction;
		private final Action errorAction;
		private final RoverActionErrorState roverActionErrorState;
		private int index = 0;
		private Action nextAction;

		public RoverModbusAction(List<Action> actions, Action successAction, Action errorAction, RoverActionErrorState roverActionErrorState) {
			super(false);
			this.actions = actions;
			this.successAction = successAction;
			this.errorAction = errorAction;
			this.roverActionErrorState = roverActionErrorState;
		}

		@Override
		protected void onUpdate() {
			super.onUpdate();

			while (true) {
				if (index >= sequentialActions.size()) { // finished all actions successfully
					nextAction = successAction;
					setDone(true);
					return;
				}
				Action activeAction = actions.get(index);
				activeAction.update();
				if (activeAction.isDone()) {
					activeAction.end();
					index++;
					if (roverActionErrorState.getErrorCount() > 0) { // an error occurred from something and the action that caused it has just finished
						nextAction = errorAction;
						setDone(true);
						return;
					}
				} else {
					break; // continue next iteration. This action is not done
				}
			}
		}

		@Override
		public Action getNextAction() {
			return nextAction;
		}
	}
}

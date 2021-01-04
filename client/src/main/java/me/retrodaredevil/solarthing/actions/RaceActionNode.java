package me.retrodaredevil.solarthing.actions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.*;
import me.retrodaredevil.solarthing.actions.environment.ActionEnvironment;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonTypeName("race")
public class RaceActionNode implements ActionNode {
	private final List<RaceNode> raceNodes;

	public RaceActionNode(@JsonProperty("racers") List<RaceNode> raceNodes) {
		this.raceNodes = raceNodes;
	}

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		return Actions.createLinkedActionRunner(new RaceAction(actionEnvironment), WhenDone.BE_DONE, true);
	}
	public static final class RaceNode {
		private final ActionNode conditionNode;
		private final ActionNode actionNode;

		@JsonCreator
		private RaceNode(List<ActionNode> actionNodeList) {
			if (actionNodeList.size() != 2) {
				throw new IllegalArgumentException("The size of the array must be 2! The first element is the condition, the second is the action");
			}

			this.conditionNode = actionNodeList.get(0);
			this.actionNode = actionNodeList.get(1);
		}
		public RaceNode(ActionNode conditionNode, ActionNode actionNode) {
			this.conditionNode = conditionNode;
			this.actionNode = actionNode;
		}
	}

	private final class RaceAction extends SimpleAction implements LinkedAction {
		private final Map<RaceNode, Action> raceNodeConditionActionMap = new LinkedHashMap<>();
		private final ActionEnvironment actionEnvironment;

		private Action nextAction;

		public RaceAction(ActionEnvironment actionEnvironment) {
			super(false);
			this.actionEnvironment = actionEnvironment;
		}

		@Override
		public Action getNextAction() {
			return nextAction;
		}

		@Override
		protected void onStart() {
			super.onStart();
			for (RaceNode raceNode : raceNodes) {
				raceNodeConditionActionMap.put(raceNode, raceNode.conditionNode.createAction(actionEnvironment));
			}
		}

		@Override
		protected void onUpdate() {
			super.onUpdate();
			for (Map.Entry<RaceNode, Action> entry : raceNodeConditionActionMap.entrySet()) {
				Action condition = entry.getValue();
				condition.update();
				if (condition.isDone()) {
					nextAction = entry.getKey().actionNode.createAction(actionEnvironment);
					break;
				}
			}
			setDone(nextAction != null);
		}
	}
}

package me.retrodaredevil.solarthing.actions;

import me.retrodaredevil.action.Action;

public class RaceActionNode implements ActionNode {
	@Override
	public Action createAction() {
		return null;
	}
	static final class RaceNode {
		private final ActionNode conditionNode;
		private final ActionNode actionNode;

		RaceNode(ActionNode conditionNode, ActionNode actionNode) {
			this.conditionNode = conditionNode;
			this.actionNode = actionNode;
		}
	}
}

package me.retrodaredevil.solarthing.program;

import me.retrodaredevil.action.node.ActionNode;

import static java.util.Objects.requireNonNull;

/**
 * Represents an action node that should be run and created by the program periodically (or created only once).
 * This may also contain "how" to run the action node or details such as if the action node should only be created once.
 */
public final class ActionNodeEntry {
	private final ActionNode actionNode;
	private final boolean runOnce;

	public ActionNodeEntry(ActionNode actionNode, boolean runOnce) {
		this.actionNode = requireNonNull(actionNode);
		this.runOnce = runOnce;
	}

	public ActionNode getActionNode() {
		return actionNode;
	}

	public boolean isRunOnce() {
		return runOnce;
	}
}

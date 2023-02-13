package me.retrodaredevil.action.node;

import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.SimpleAction;

import static java.util.Objects.requireNonNull;

public class InvertAction extends SimpleAction {
	private final Action action;
	protected InvertAction(Action action) {
		super(true);
		this.action = requireNonNull(action);
	}

	@Override
	protected void onUpdate() {
		super.onUpdate();
		action.update();
		setDone(!action.isDone());
	}

	@Override
	protected void onEnd(boolean peacefullyEnded) {
		super.onEnd(peacefullyEnded);
		action.end();
	}
}

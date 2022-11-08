package me.retrodaredevil.solarthing.actions.error;

import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.LinkedAction;
import me.retrodaredevil.action.SimpleAction;

import java.util.List;

public class TryErrorStateAction extends SimpleAction implements LinkedAction {

	private final List<Action> actions;
	private final Action successAction;
	private final Action errorAction;
	private final ActionErrorState actionErrorState;
	private int index = 0;
	private Action nextAction;

	public TryErrorStateAction(List<Action> actions, Action successAction, Action errorAction, ActionErrorState actionErrorState) {
		super(false);
		this.actions = actions;
		this.successAction = successAction;
		this.errorAction = errorAction;
		this.actionErrorState = actionErrorState;
	}

	@Override
	protected void onUpdate() {
		super.onUpdate();

		while (true) {
			if (index >= actions.size()) { // finished all actions successfully
				nextAction = successAction;
				setDone(true);
				return;
			}
			Action activeAction = actions.get(index);
			activeAction.update();
			if (activeAction.isDone()) {
				activeAction.end();
				index++;
				if (actionErrorState.getErrorCount() > 0) { // an error occurred from something and the action that caused it has just finished
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

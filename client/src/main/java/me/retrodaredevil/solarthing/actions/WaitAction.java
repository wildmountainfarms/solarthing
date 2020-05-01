package me.retrodaredevil.solarthing.actions;

import me.retrodaredevil.action.SimpleAction;

public class WaitAction extends SimpleAction {
	private final long waitMillis;

	private Long startTimeMillis = null;

	public WaitAction(long waitMillis) {
		super(false);
		this.waitMillis = waitMillis;
	}

	@Override
	protected void onStart() {
		super.onStart();
		startTimeMillis = System.currentTimeMillis();
	}

	@Override
	protected void onUpdate() {
		super.onUpdate();
		setDone(startTimeMillis + waitMillis <= System.currentTimeMillis());
	}
}

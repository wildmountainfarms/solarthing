package me.retrodaredevil.action.node;

import me.retrodaredevil.action.SimpleAction;

import java.time.Duration;

import static java.util.Objects.requireNonNull;

public class WaitAction extends SimpleAction {
	private final Duration waitDuration;

	private Long startTimeNanos = null;

	public WaitAction(Duration waitDuration) {
		super(false);
		requireNonNull(this.waitDuration = waitDuration);
	}

	@Override
	protected void onStart() {
		super.onStart();
		startTimeNanos = System.nanoTime();
	}

	@Override
	protected void onUpdate() {
		super.onUpdate();
		setDone(System.nanoTime() - startTimeNanos >= waitDuration.toNanos());
	}
}

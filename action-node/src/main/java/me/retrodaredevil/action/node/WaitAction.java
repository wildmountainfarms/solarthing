package me.retrodaredevil.action.node;

import me.retrodaredevil.action.SimpleAction;
import me.retrodaredevil.action.node.util.NanoTimeProvider;

import java.time.Duration;

import static java.util.Objects.requireNonNull;

public class WaitAction extends SimpleAction {
	private final NanoTimeProvider nanoTimeProvider;
	private final Duration waitDuration;

	private Long startTimeNanos = null;

	public WaitAction(NanoTimeProvider nanoTimeProvider, Duration waitDuration) {
		super(false);
		requireNonNull(this.nanoTimeProvider = nanoTimeProvider);
		requireNonNull(this.waitDuration = waitDuration);
	}

	@Override
	protected void onStart() {
		super.onStart();
		startTimeNanos = nanoTimeProvider.getNanos();
	}

	@Override
	protected void onUpdate() {
		super.onUpdate();
		setDone(nanoTimeProvider.getNanos() - startTimeNanos >= waitDuration.toNanos());
	}
}

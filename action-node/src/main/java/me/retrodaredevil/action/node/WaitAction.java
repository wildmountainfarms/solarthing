package me.retrodaredevil.action.node;

import me.retrodaredevil.action.SimpleAction;
import me.retrodaredevil.action.node.util.NanoTimeProvider;
import org.jspecify.annotations.Nullable;

import java.time.Duration;

import static java.util.Objects.requireNonNull;

public class WaitAction extends SimpleAction {
	private final NanoTimeProvider nanoTimeProvider;
	private final Duration waitDuration;

	private @Nullable Long startTimeNanos = null;

	public WaitAction(NanoTimeProvider nanoTimeProvider, Duration waitDuration) {
		super(false);
		this.nanoTimeProvider = requireNonNull(nanoTimeProvider);
		this.waitDuration = requireNonNull(waitDuration);
	}

	@Override
	protected void onStart() {
		super.onStart();
		startTimeNanos = nanoTimeProvider.getNanos();
	}

	@Override
	protected void onUpdate() {
		super.onUpdate();
		setDone(nanoTimeProvider.getNanos() - requireNonNull(startTimeNanos, "startTimeNanos") >= waitDuration.toNanos());
	}
}

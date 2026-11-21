package me.retrodaredevil.action.node;

import me.retrodaredevil.action.SimpleAction;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.time.Clock;
import java.time.LocalTime;

/**
 * An action that is always done when the wall clock time of the configured timezone is between the provided {@code start} (inclusive) and {@code end} (exclusive)
 * <p>
 * Or simply: "The action ends if the current time is between these two times"
 */
@NullMarked
public class WallClockAction extends SimpleAction {
	private final Clock clock;
	private final @Nullable LocalTime startInclusive;
	private final @Nullable LocalTime endExclusive;

	public WallClockAction(Clock clock, @Nullable LocalTime startInclusive, @Nullable LocalTime endExclusive) {
		super(true);
		this.clock = clock;
		this.startInclusive = startInclusive;
		this.endExclusive = endExclusive;
	}

	@Override
	protected void onUpdate() {
		super.onUpdate();
		LocalTime localTime = clock.instant().atZone(clock.getZone()).toLocalTime();
		setDone((startInclusive == null || startInclusive.compareTo(localTime) <= 0) && (endExclusive == null || localTime.compareTo(endExclusive) < 0));
	}
}

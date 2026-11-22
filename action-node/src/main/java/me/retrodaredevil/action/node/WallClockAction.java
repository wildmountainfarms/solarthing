package me.retrodaredevil.action.node;

import me.retrodaredevil.action.SimpleAction;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * An action that is always done when the wall clock time of the configured timezone is between the provided {@code start} (inclusive) and {@code end} (exclusive)
 * <p>
 * Or simply: "The action ends if the current time is between these two times"
 */
@NullMarked
public class WallClockAction extends SimpleAction {
	private final Clock clock;
	private final LocalTime startInclusive;
	private final LocalTime endExclusive;
	private final @Nullable Set<DayOfWeek> days;

	public WallClockAction(Clock clock, LocalTime startInclusive, LocalTime endExclusive, @Nullable Collection<DayOfWeek> days) {
		super(true);
		this.clock = requireNonNull(clock, "clock");
		this.startInclusive = requireNonNull(startInclusive, "startInclusive");
		this.endExclusive = requireNonNull(endExclusive, "endExclusive");
		this.days = days == null ? null : EnumSet.copyOf(days);
	}

	@Override
	protected void onUpdate() {
		super.onUpdate();
		ZonedDateTime zonedDateTime = clock.instant().atZone(clock.getZone());
		DayOfWeek dayOfWeek = zonedDateTime.getDayOfWeek();
		LocalTime localTime = zonedDateTime.toLocalTime();
		final boolean isInRange;
		final boolean doesDayOfWeekMatch;
		if (endExclusive.compareTo(startInclusive) <= 0) {
			// this covers these cases, for example:
			//   23:00 to 03:00
			//   22:00 to 00:00
			//   00:00 to 00:00 (the entire day)
			boolean inRangeFirstPartOfPeriod = startInclusive.compareTo(localTime) <= 0;
			boolean inRangeSecondPartOfPeriod = localTime.compareTo(endExclusive) < 0;
			isInRange = inRangeFirstPartOfPeriod || inRangeSecondPartOfPeriod;
			if (days == null) {
				doesDayOfWeekMatch = true;
			} else {
				if (inRangeFirstPartOfPeriod) {
					doesDayOfWeekMatch = days.contains(dayOfWeek);
				} else if (inRangeSecondPartOfPeriod) {
					doesDayOfWeekMatch = days.contains(dayOfWeek.minus(1));
				} else {
					// this value doesn't really matter, and note that false isn't technically the most correct, but that's OK
					doesDayOfWeekMatch = false;
				}
			}
		} else {
			// this covers the "normal" case, for example:
			isInRange = startInclusive.compareTo(localTime) <= 0 && localTime.compareTo(endExclusive) < 0;
			doesDayOfWeekMatch = days == null || days.contains(dayOfWeek);
		}
		setDone(isInRange && doesDayOfWeekMatch);
	}
}

package me.retrodaredevil.action.node;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.action.node.environment.ClockEnvironment;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

import static java.util.Objects.requireNonNull;


@JsonTypeName("wallclock")
@NullMarked
public class WallClockActionNode implements ActionNode {

	private final LocalTime startInclusive;
	private final LocalTime endExclusive;
	private final @Nullable Set<DayOfWeek> days;

	@JsonCreator
	public WallClockActionNode(
			@JsonProperty(value = "start", required = true) LocalTime startInclusive,
			@JsonProperty(value = "end", required = true) LocalTime endExclusive,
			@JsonProperty("days") @Nullable Set<DayOfWeek> days) {
		this.startInclusive = requireNonNull(startInclusive, "start is required");
		this.endExclusive = requireNonNull(endExclusive, "end is required");
		this.days = days;
	}

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		Clock clock = actionEnvironment.getInjectEnvironment().get(ClockEnvironment.class).clock();
		return new WallClockAction(clock, startInclusive, endExclusive, days);
	}
}

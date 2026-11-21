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
import java.time.LocalTime;


@JsonTypeName("wallclock")
@NullMarked
public class WallClockActionNode implements ActionNode {

	private final @Nullable LocalTime startInclusive;
	private final @Nullable LocalTime endExclusive;

	@JsonCreator
	public WallClockActionNode(
			@JsonProperty("start") @Nullable LocalTime startInclusive,
			@JsonProperty("end") @Nullable LocalTime endExclusive) {
		this.startInclusive = startInclusive;
		this.endExclusive = endExclusive;
	}

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		Clock clock = actionEnvironment.getInjectEnvironment().get(ClockEnvironment.class).clock();
		return new WallClockAction(clock, startInclusive, endExclusive);
	}
}

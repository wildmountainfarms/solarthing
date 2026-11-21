package me.retrodaredevil.action.node.environment;

import org.jspecify.annotations.NullMarked;

import java.time.Clock;

@NullMarked
public record ClockEnvironment(Clock clock) {
}

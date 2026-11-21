package me.retrodaredevil.solarthing.actions.environment;

import org.jspecify.annotations.NullMarked;

import java.time.Clock;

@NullMarked
public record ClockEnvironment(Clock clock) {
}

package me.retrodaredevil.solarthing.util.integration;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class TrapezoidalRuleAccumulator extends MutableIntegralAccumulator {
	@Override
	protected double getAmountToAdd(double deltaHours, double lastY, double y) {
		return deltaHours * (lastY + y) / 2;
	}
}

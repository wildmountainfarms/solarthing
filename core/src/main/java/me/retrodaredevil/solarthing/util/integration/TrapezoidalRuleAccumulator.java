package me.retrodaredevil.solarthing.util.integration;

public class TrapezoidalRuleAccumulator extends MutableIntegralAccumulator {
	@Override
	protected double getAmountToAdd(double deltaX, double lastY, double y) {
		return deltaX * (lastY + y) / 2;
	}
}

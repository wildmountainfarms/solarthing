package me.retrodaredevil.solarthing.util.integration;

public class RightRiemannSumAccumulator extends MutableIntegralAccumulator {
	@Override
	protected double getAmountToAdd(double deltaX, double lastY, double y) {
		return deltaX * y;
	}
}

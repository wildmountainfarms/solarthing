package me.retrodaredevil.solarthing.util.integration;

public class LeftRiemannSumAccumulator extends MutableIntegralAccumulator {
	@Override
	protected double getAmountToAdd(double deltaX, double lastY, double y) {
		return deltaX * lastY;
	}
}

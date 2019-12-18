package me.retrodaredevil.solarthing.util.integration;

abstract class MutableIntegralAccumulator implements MutableIntegral {
	private double integral = 0;

	private Double lastX = null;
	private double lastY;

	@Override
	public final void add(double x, double y) {
		final Double lastX = this.lastX;
		if(lastX == null){
			this.lastX = x;
			this.lastY = y;
			return;
		}
		double delta = x - lastX;
		if(delta < 0){
			throw new IllegalArgumentException("x cannot be less than the last x value! x=" + x + " lastX=" + lastX);
		}
		integral += getAmountToAdd(delta, lastY, y);
		this.lastX = x;
		lastY = y;
	}
	protected abstract double getAmountToAdd(double deltaX, double lastY, double y);

	@Override
	public final void reset(boolean fullReset) {
		integral = 0;
		if(fullReset){
			lastX = null;
		}
	}
	@Override public final void reset() { MutableIntegral.super.reset(); }

	@Override
	public final double getIntegral() {
		return integral;
	}
}

package me.retrodaredevil.solarthing.util.integration;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;

@NullMarked
abstract class MutableIntegralAccumulator implements MutableIntegral {
	private double integral = 0;
	private double compensation = 0;

	private @Nullable Instant lastTime = null;
	private double lastY;

	@Override
	public final void add(Instant time, double y) {
		final Instant lastTime = this.lastTime;
		if(lastTime == null){
			this.lastTime = time;
			this.lastY = y;
			return;
		}
		if(time.isBefore(lastTime)){
			throw new IllegalArgumentException("time cannot precede the last timestamp! time=" + time + " lastTime=" + lastTime);
		}
		Duration elapsed = Duration.between(lastTime, time);
		// Subtract timestamps before converting to double to preserve short intervals.
		double deltaHours = elapsed.getSeconds() / 3600.0 + elapsed.getNano() / 3_600_000_000_000.0;
		double amount = getAmountToAdd(deltaHours, lastY, y);

		double correctedAmount = amount - compensation;
		double nextIntegral = integral + correctedAmount;
		compensation = (nextIntegral - integral) - correctedAmount;
		integral = nextIntegral;
		this.lastTime = time;
		lastY = y;
	}
	protected abstract double getAmountToAdd(double deltaHours, double lastY, double y);

	@Override
	public final void reset(boolean fullReset) {
		integral = 0;
		compensation = 0;
		if(fullReset){
			lastTime = null;
		}
	}
	@Override public final void reset() { MutableIntegral.super.reset(); }

	@Override
	public void setIntegral(double integral) {
		this.integral = integral;
		this.compensation = 0;
	}

	@Override
	public final double getIntegral() {
		return integral;
	}
}

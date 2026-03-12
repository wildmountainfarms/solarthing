package me.retrodaredevil.solarthing.util.integration;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface MutableIntegral {
	/**
	 *
	 * @param x The x value (usually time)
	 * @param y The y value (the thing that is varying)
	 * @throws IllegalArgumentException If {@code x} is less than the last passed x value
	 */
	void add(double x, double y);
	void reset(boolean fullReset);
	default void reset(){ reset(false); }
	void setIntegral(double value);

	double getIntegral();
}

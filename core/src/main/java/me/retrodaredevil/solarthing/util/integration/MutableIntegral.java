package me.retrodaredevil.solarthing.util.integration;

import org.jspecify.annotations.NullMarked;

import java.time.Instant;

/**
 * Accumulates an integral over time, measured in hours multiplied by the units of y.
 * For example, watt readings produce watt-hours.
 */
@NullMarked
public interface MutableIntegral {
	/**
	 * The first reading establishes the starting point without adding to the integral.
	 * Equal timestamps are allowed and update the reading without advancing time.
	 *
	 * @param time The timestamp of the reading
	 * @param y The y value (the thing that is varying)
	 * @throws IllegalArgumentException If {@code time} precedes the previous reading
	 */
	void add(Instant time, double y);
	void reset(boolean fullReset);
	default void reset(){ reset(false); }
	void setIntegral(double value);

	double getIntegral();
}

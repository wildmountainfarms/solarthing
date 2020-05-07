package me.retrodaredevil.solarthing.solar.common;

import me.retrodaredevil.solarthing.packets.identification.Identifiable;
import me.retrodaredevil.solarthing.annotations.Nullable;

public interface DailyData extends Identifiable {
	/**
	 * NOTE: If {@code previousDailyData} is data from a {@link DailyData} generated after this, the returned result is
	 * undefined.
	 *
	 * @param previousDailyData A {@link DailyData} of the same type of this to test if it was from a previous day.
	 * @return true if this {@link DailyData} is a new/next day compared to {@code previousDailyData}
	 * @throws IllegalArgumentException If {@code previousDailyData} is not the same type as this. Optional.
	 */
	boolean isNewDay(DailyData previousDailyData);

	/**
	 * Should be serialized as "startDateMillis" if serialized at all
	 *
	 * @return The start time in UTC millis or null if not supported/not yet implemented.
	 */
	@Nullable
	default Long getStartDateMillis() { return null; }
}

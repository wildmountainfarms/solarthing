package me.retrodaredevil.solarthing.solar.common;

public interface DailyData {
	float getDailyKWH();
	@Deprecated
	default String getDailyKWHString(){
		return "" + getDailyKWH();
	}
	int getDailyAH();
	
	/**
	 * NOTE: If {@code previousDailyData} is data from a {@link DailyData} generated after this, the returned result is
	 * undefined.
	 *
	 * @param previousDailyData A {@link DailyData} of the same type of this to test if it was from a previous day.
	 * @return true if this {@link DailyData} is a new/next day compared to {@code previousDailyData}
	 * @throws IllegalArgumentException If {@code previousDailyData} is not the same type as this. Optional.
	 */
	default boolean isNewDay(DailyData previousDailyData){
		return getDailyKWH() < previousDailyData.getDailyKWH() || getDailyAH() < previousDailyData.getDailyAH();
	}
}

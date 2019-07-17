package me.retrodaredevil.solarthing.solar.common;

public interface DailyData {
	float getDailyKWH();
	@Deprecated
	default String getDailyKWHString(){
		return "" + getDailyKWH();
	}
	int getDailyAH();
}

package me.retrodaredevil.solarthing.solar.common;

public interface DailyUpdatePacket {
	Long getLastResetTimeMillis();
	Long getLastIncrementTimeMillis();

}

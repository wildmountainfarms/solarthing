package me.retrodaredevil.solarthing.solar.common;

/**
 * @deprecated This is a work in progress class that might eventually be used to give the MX's dailyKWH more data to indicate when it last reset or incremented
 */
@Deprecated
public interface DailyUpdatePacket {
	Long getLastResetTimeMillis();
	Long getLastIncrementTimeMillis();

}

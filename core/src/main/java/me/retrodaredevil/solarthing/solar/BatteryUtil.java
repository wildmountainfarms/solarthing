package me.retrodaredevil.solarthing.solar;

public final class BatteryUtil {
	private BatteryUtil() { throw new UnsupportedOperationException(); }
	public static int getNumberOfCells(float batteryVoltage) {
		if (batteryVoltage < 18.0) { // 12V
			return 6;
		} else if (batteryVoltage < 33.0) { // 24V
			return 12;
		} else { // 48V
			return 24;
		}
	}
	public static float getOutbackCompensatedBatteryVoltage(float batteryVoltage, int temperatureCelsius) {
		return getOutbackCompensatedBatteryVoltage(batteryVoltage, temperatureCelsius, 5);
	}
	public static float getOutbackCompensatedBatteryVoltage(float batteryVoltage, int temperatureCelsius, int slope_mVPerCellPerDegreeCelsius) {
		int normalizedTemperature = Math.max(-20, Math.min(20, temperatureCelsius - 25)); // outback FXs limit how much to compensate by
		int numberOfCells = BatteryUtil.getNumberOfCells(batteryVoltage);
		int delta_mV = numberOfCells * slope_mVPerCellPerDegreeCelsius * normalizedTemperature;
		return batteryVoltage + delta_mV / 1000.0f;
	}
}

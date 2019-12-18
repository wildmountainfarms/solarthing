package me.retrodaredevil.solarthing.solar.outback.fx.supplementary;

import me.retrodaredevil.solarthing.solar.common.DailyBatteryVoltage;

public interface DailyFXPacket extends DailyBatteryVoltage {
	float getDailyInverterKWH();
	float getDailyInverterAH();

	float getDailyChargerKWH();
	float getDailyChargerAH();

	float getDailyBuyKWH();
	float getDailyBuyAH();

	float getDailySellKWH();
	float getDailySellAH();
}

package me.retrodaredevil.solarthing.solar.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.packets.support.Support;

public interface AccumulatedChargeController extends ChargeController {
	@JsonProperty("dailyKWH")
	float getDailyKWH();
	@JsonProperty("dailyAH")
	int getDailyAH();

	/**
	 * Should be serialized as "dailyAHSupport" if serialized at all. Should be serialized using {@link Support#toString()}
	 * @return A {@link Support} enum constant indicating whether or not {@link #getDailyAH()} is supported
	 */
	default Support getDailyAHSupport(){ return Support.UNKNOWN; }
}

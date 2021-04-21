package me.retrodaredevil.solarthing.solar.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.packets.support.Support;

import me.retrodaredevil.solarthing.annotations.NotNull;

public interface AccumulatedChargeController extends ChargeController {
	@JsonProperty("dailyKWH")
	float getDailyKWH();
	@JsonProperty("dailyAH")
	int getDailyAH();

	/**
	 * Should be serialized as "dailyAHSupport" if serialized at all. Should be serialized using {@link Support#toString()}
	 * <p>
	 * This never returns null. When deserializing, if no value is present, {@link Support#UNKNOWN} is the default value.
	 * @return A {@link Support} enum constant indicating whether or not {@link #getDailyAH()} is supported
	 */
	default @NotNull Support getDailyAHSupport(){ return Support.UNKNOWN; }
}

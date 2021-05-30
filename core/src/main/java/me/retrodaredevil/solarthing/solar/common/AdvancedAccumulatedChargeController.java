package me.retrodaredevil.solarthing.solar.common;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface AdvancedAccumulatedChargeController extends AccumulatedChargeController {
	@JsonProperty("dailyKWHConsumption")
	float getDailyKWHConsumption();


	@JsonProperty("cumulativeKWH")
	float getCumulativeKWH();
	@JsonProperty("cumulativeKWHConsumption")
	float getCumulativeKWHConsumption();
}

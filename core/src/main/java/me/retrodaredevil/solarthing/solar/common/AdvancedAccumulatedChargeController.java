package me.retrodaredevil.solarthing.solar.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface AdvancedAccumulatedChargeController extends AccumulatedChargeController {
	@JsonProperty("dailyKWHConsumption")
	float getDailyKWHConsumption();


	@JsonProperty("cumulativeKWH")
	float getCumulativeKWH();
	@JsonProperty("cumulativeKWHConsumption")
	float getCumulativeKWHConsumption();
}

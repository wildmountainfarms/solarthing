package me.retrodaredevil.solarthing.solar.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.packets.annotations.ValidSinceVersion;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverStatusPacket;

public interface AdvancedAccumulatedChargeController extends AccumulatedChargeController {
	@JsonProperty("dailyKWHConsumption")
	float getDailyKWHConsumption();


	@JsonProperty("cumulativeKWH")
	float getCumulativeKWH();
	@JsonProperty("cumulativeKWHConsumption")
	float getCumulativeKWHConsumption();
}

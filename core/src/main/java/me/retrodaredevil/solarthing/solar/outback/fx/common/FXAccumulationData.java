package me.retrodaredevil.solarthing.solar.outback.fx.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface FXAccumulationData {
	@JsonProperty("inverterKWH")
	float getInverterKWH();
	@JsonProperty("chargerKWH")
	float getChargerKWH();
	@JsonProperty("buyKWH")
	float getBuyKWH();
	@JsonProperty("sellKWH")
	float getSellKWH();
}

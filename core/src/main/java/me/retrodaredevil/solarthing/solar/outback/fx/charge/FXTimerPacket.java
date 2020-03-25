package me.retrodaredevil.solarthing.solar.outback.fx.charge;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface FXTimerPacket {
	long getRemainingTime();
	TimerType getTimerType();
	boolean isCountingDown();

	@JsonProperty("masterFXAddress")
	int getMasterFXAddress();

	enum TimerType {
		ABSORB,
		EQ,
		FLOAT
	}
}

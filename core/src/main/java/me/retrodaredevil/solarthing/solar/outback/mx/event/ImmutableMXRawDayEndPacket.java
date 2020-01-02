package me.retrodaredevil.solarthing.solar.outback.mx.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.packets.support.Support;

public class ImmutableMXRawDayEndPacket implements MXRawDayEndPacket {

	private final float dailyKWH;
	private final int dailyAH;
	private final Support dailyAHSupport;

	@JsonCreator
	public ImmutableMXRawDayEndPacket(
			@JsonProperty(value = "dailyKWH", required = true) float dailyKWH,
			@JsonProperty(value = "dailyAH", required = true) int dailyAH,
			@JsonProperty(value = "dailyAHSupport", required = true) Support dailyAHSupport
	) {
		this.dailyKWH = dailyKWH;
		this.dailyAH = dailyAH;
		this.dailyAHSupport = dailyAHSupport;
	}


	@Override
	public float getDailyKWH() {
		return dailyKWH;
	}

	@Override
	public int getDailyAH() {
		return dailyAH;
	}

	@Override
	public Support getDailyAHSupport() {
		return dailyAHSupport;
	}
}

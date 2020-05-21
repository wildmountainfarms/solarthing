package me.retrodaredevil.solarthing.solar.outback.mx.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;
import me.retrodaredevil.solarthing.packets.identification.SupplementaryIdentifier;
import me.retrodaredevil.solarthing.packets.identification.UnknownSupplementaryIdentifier;
import me.retrodaredevil.solarthing.packets.support.Support;
import me.retrodaredevil.solarthing.solar.outback.OutbackIdentifier;
import me.retrodaredevil.solarthing.solar.outback.fx.FXIdentityInfo;

public class ImmutableMXDailyData implements MXDailyData {

	private final SupplementaryIdentifier identifier;
	private final IdentityInfo identityInfo;

	private final int address;
	private final int errorModeValue;
	private final long startDateMillis;
	private final float dailyKWH;
	private final int dailyAH;
	private final Support dailyAHSupport;
	private final float dailyMinBatteryVoltage;
	private final float dailyMaxBatteryVoltage;

	@JsonCreator
	public ImmutableMXDailyData(
			@JsonProperty(value = "address", required = true) int address,
			@JsonProperty(value = "errorModeValue", required = true) int errorModeValue,
			@JsonProperty(value = "startDateMillis", required = true) long startDateMillis,
			@JsonProperty(value = "dailyKWH", required = true) float dailyKWH,
			@JsonProperty(value = "dailyAH", required = true) int dailyAH,
			@JsonProperty(value = "dailyAHSupport", required = true) Support dailyAHSupport,
			@JsonProperty(value = "dailyMinBatteryVoltage", required = true) float dailyMinBatteryVoltage,
			@JsonProperty(value = "dailyMaxBatteryVoltage", required = true) float dailyMaxBatteryVoltage
	) {
		this.address = address;
		this.errorModeValue = errorModeValue;
		this.startDateMillis = startDateMillis;

		this.dailyKWH = dailyKWH;
		this.dailyAH = dailyAH;
		this.dailyAHSupport = dailyAHSupport;

		this.dailyMinBatteryVoltage = dailyMinBatteryVoltage;
		this.dailyMaxBatteryVoltage = dailyMaxBatteryVoltage;
		identifier = new UnknownSupplementaryIdentifier<>(new OutbackIdentifier(address));
		identityInfo = new FXIdentityInfo(address);
	}

	@Override
	public @NotNull SupplementaryIdentifier getIdentifier() {
		return identifier;
	}

	@Override
	public @NotNull IdentityInfo getIdentityInfo() {
		return identityInfo;
	}

	@Override
	public int getAddress() {
		return address;
	}

	@Override
	public int getErrorModeValue() {
		return errorModeValue;
	}

	@Override
	public @NotNull Long getStartDateMillis() {
		return startDateMillis;
	}

	@Deprecated
	@Override
	public float getDailyKWH() {
		return dailyKWH;
	}

	@Deprecated
	@Override
	public int getDailyAH() {
		return dailyAH;
	}

	@Override
	public @NotNull Support getDailyAHSupport() {
		return dailyAHSupport;
	}

	@Override
	public float getDailyMinBatteryVoltage() {
		return dailyMinBatteryVoltage;
	}

	@Override
	public float getDailyMaxBatteryVoltage() {
		return dailyMaxBatteryVoltage;
	}
}

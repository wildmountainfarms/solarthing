package me.retrodaredevil.solarthing.solar.outback.fx.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

public class ImmutableFXDailyData implements FXDailyData {

	private final int address;

	private final Long startDateMillis;

	private final float dailyMinBatteryVoltage;
	private final float dailyMaxBatteryVoltage;

	private final float inverterKWH;
	private final float chargerKWH;
	private final float buyKWH;
	private final float sellKWH;

	private final Collection<Integer> operationalModeValues;
	private final int errorModeValue;
	private final int warningModeValue;
	private final int miscValue;
	private final Collection<Integer> acModeValues;

	@JsonCreator
	public ImmutableFXDailyData(
			@JsonProperty(value = "address", required = true) int address,
			@JsonProperty(value = "startDateMillis") Long startDateMillis,
			@JsonProperty(value = "dailyMinBatteryVoltage", required = true) float dailyMinBatteryVoltage, @JsonProperty(value = "dailyMaxBatteryVoltage", required = true) float dailyMaxBatteryVoltage, @JsonProperty(value = "inverterKWH", required = true) float inverterKWH, @JsonProperty(value = "chargerKWH", required = true) float chargerKWH, @JsonProperty(value = "buyKWH", required = true) float buyKWH, @JsonProperty(value = "sellKWH", required = true) float sellKWH,
			@JsonProperty(value = "operationalModeValues", required = true) Collection<Integer> operationalModeValues, @JsonProperty(value = "errorModeValue", required = true) int errorModeValue, @JsonProperty(value = "warningModeValue", required = true) int warningModeValue, @JsonProperty(value = "miscValue", required = true) int miscValue, @JsonProperty(value = "acModeValues", required = true) Collection<Integer> acModeValues
	) {
		this.address = address;
		this.startDateMillis = startDateMillis;
		this.dailyMinBatteryVoltage = dailyMinBatteryVoltage;
		this.dailyMaxBatteryVoltage = dailyMaxBatteryVoltage;
		this.inverterKWH = inverterKWH;
		this.chargerKWH = chargerKWH;
		this.buyKWH = buyKWH;
		this.sellKWH = sellKWH;
		this.operationalModeValues = operationalModeValues;
		this.errorModeValue = errorModeValue;
		this.warningModeValue = warningModeValue;
		this.miscValue = miscValue;
		this.acModeValues = acModeValues;
	}

	@Override
	public int getAddress() {
		return address;
	}

	@Override
	public Long getStartDateMillis() {
		return startDateMillis;
	}

	@Override public float getDailyMinBatteryVoltage() { return dailyMinBatteryVoltage; }
	@Override public float getDailyMaxBatteryVoltage() { return dailyMaxBatteryVoltage; }

	@Override public float getInverterKWH() { return inverterKWH; }
	@Override public float getChargerKWH() { return chargerKWH; }
	@Override public float getBuyKWH() { return buyKWH; }
	@Override public float getSellKWH() { return sellKWH; }

	@Override public Collection<Integer> getOperationalModeValues() { return operationalModeValues; }
	@Override public int getErrorModeValue() { return errorModeValue; }
	@Override public int getWarningModeValue() { return warningModeValue; }
	@Override public int getMiscValue() { return miscValue; }
	@Override public Collection<Integer> getACModeValues() { return acModeValues; }
}

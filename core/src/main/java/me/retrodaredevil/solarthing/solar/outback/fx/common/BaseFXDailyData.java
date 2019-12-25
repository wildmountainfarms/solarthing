package me.retrodaredevil.solarthing.solar.outback.fx.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import me.retrodaredevil.solarthing.packets.DocumentedPacketType;
import me.retrodaredevil.solarthing.packets.identification.DefaultSupplementaryIdentifier;
import me.retrodaredevil.solarthing.packets.identification.SupplementaryIdentifiable;
import me.retrodaredevil.solarthing.packets.identification.SupplementaryIdentifier;
import me.retrodaredevil.solarthing.solar.outback.OutbackIdentifier;

import java.util.Collection;

public abstract class BaseFXDailyData implements FXDailyData, SupplementaryIdentifiable {

	@JsonSerialize
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

	private final transient SupplementaryIdentifier supplementaryIdentifier;

	public BaseFXDailyData(
			DocumentedPacketType packetType,
			FXDailyData dailyData,
			OutbackIdentifier outbackIdentifier
	) {
		this.startDateMillis = dailyData.getStartDateMillis();
		this.dailyMinBatteryVoltage = dailyData.getDailyMinBatteryVoltage();
		this.dailyMaxBatteryVoltage = dailyData.getDailyMaxBatteryVoltage();
		this.inverterKWH = dailyData.getInverterKWH();
		this.chargerKWH = dailyData.getChargerKWH();
		this.buyKWH = dailyData.getBuyKWH();
		this.sellKWH = dailyData.getSellKWH();
		this.operationalModeValues = dailyData.getOperationalModeValues();
		this.errorModeValue = dailyData.getWarningModeValue();
		this.warningModeValue = dailyData.getWarningModeValue();
		this.miscValue = dailyData.getMiscValue();
		this.acModeValues = dailyData.getACModeValues();

		this.address = outbackIdentifier.getAddress();
		this.supplementaryIdentifier = new DefaultSupplementaryIdentifier<>(outbackIdentifier, packetType.toString());
	}

	@Override
	public SupplementaryIdentifier getIdentifier() {
		return supplementaryIdentifier;
	}

	@JsonProperty("startDateMillis")
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

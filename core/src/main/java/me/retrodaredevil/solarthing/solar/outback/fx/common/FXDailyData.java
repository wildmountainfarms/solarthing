package me.retrodaredevil.solarthing.solar.outback.fx.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.GraphQLInclude;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.Modes;
import me.retrodaredevil.solarthing.solar.common.DailyBatteryVoltage;
import me.retrodaredevil.solarthing.solar.common.DailyData;
import me.retrodaredevil.solarthing.solar.common.ErrorReporter;
import me.retrodaredevil.solarthing.solar.outback.OutbackData;
import me.retrodaredevil.solarthing.solar.outback.fx.ACMode;
import me.retrodaredevil.solarthing.solar.outback.fx.FXErrorMode;
import me.retrodaredevil.solarthing.solar.outback.fx.OperationalMode;
import me.retrodaredevil.solarthing.solar.outback.fx.extra.DailyFXPacket;
import me.retrodaredevil.solarthing.annotations.Nullable;

import java.util.Collection;
import java.util.Set;

@JsonExplicit
public interface FXDailyData extends OutbackData, DailyBatteryVoltage, ErrorReporter, FXWarningReporter, FXMiscReporter, FXAccumulationData {
	@Override
	default boolean isNewDay(DailyData previousDailyData){
		if (!(previousDailyData instanceof FXDailyData)) {
			throw new IllegalArgumentException("previousDailyData is not a FXDailyData! It's: " + previousDailyData.getClass().getName());
		}
		DailyFXPacket previous = (DailyFXPacket) previousDailyData;
		/*
		In the early versions of DailyFXPacket, startDateMillis was not serialized. In new versions, they are always serialized.
		This is why we have to perform a null check and deal with either one being null. (One could be a packet from a previous version)

		However, this happened for like less than a week such a long time ago, that I'm thinking about ditching support for those packets.
		As of 2020.05.09, there's not really a way to ditch support for packets gracefully. Maybe there will be in the future.
		 */
		Long dateMillis = getStartDateMillis();
		Long previousMillis = previous.getStartDateMillis();
		if(dateMillis != null && previousMillis != null){
			return dateMillis > previousMillis;
		}
		return getInverterKWH() < previous.getInverterKWH() ||
				getChargerKWH() < previous.getChargerKWH() ||
				getBuyKWH() < previous.getBuyKWH() ||
				getSellKWH() < previous.getSellKWH();
	}

	@JsonProperty("startDateMillis")
	@Override
	@Nullable
	Long getStartDateMillis();

	// FX Accumulation Data

	@JsonProperty("operationalModeValues")
	@NotNull Collection<@NotNull Integer> getOperationalModeValues();
	@GraphQLInclude("operationalModes")
	default @NotNull Set<@NotNull OperationalMode> getOperationalModes(){ return Modes.getActiveModes(OperationalMode.class, getOperationalModeValues()); }

	/**
	 * Should be serialized as "errorModeValue"
	 * @return The bit-masked value representing all the error modes that were active during one day
	 * @deprecated before an update on 2020.07.29, this was always the same as {@link #getWarningModeValue()}
	 */
	@Deprecated
	@JsonProperty("errorModeValue")
	@Override
	int getErrorModeValue();
	@Deprecated
	@Override
	default Set<@NotNull FXErrorMode> getErrorModes(){ return Modes.getActiveModes(FXErrorMode.class, getErrorModeValue()); }

	@JsonProperty("warningModeValue")
	@Override
	int getWarningModeValue();

	@JsonProperty("miscValue")
	@Override
	int getMiscValue();

	@JsonProperty("acModeValues")
	@NotNull Collection<@NotNull Integer> getACModeValues();
	@GraphQLInclude("acModes")
	default @NotNull Set<@NotNull ACMode> getACModes(){ return Modes.getActiveModes(ACMode.class, getACModeValues()); }
}

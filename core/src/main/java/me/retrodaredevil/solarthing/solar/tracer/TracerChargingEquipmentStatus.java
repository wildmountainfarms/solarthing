package me.retrodaredevil.solarthing.solar.tracer;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.GraphQLInclude;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.Modes;
import me.retrodaredevil.solarthing.solar.common.ErrorReporter;
import me.retrodaredevil.solarthing.solar.tracer.mode.ChargingEquipmentError;
import me.retrodaredevil.solarthing.solar.tracer.mode.ChargingStatus;
import me.retrodaredevil.solarthing.solar.tracer.mode.InputVoltageStatus;

import java.util.Set;

public interface TracerChargingEquipmentStatus extends ErrorReporter {

	@JsonProperty("chargingEquipmentStatusValue")
	int getChargingEquipmentStatus();

	default int getInputVoltageStatusValue() { return getChargingEquipmentStatus() >> 14; }
	default int getChargingStatusValue() { return (getChargingEquipmentStatus() >> 2) & 0b11; }
	@Override
	default @NotNull Set<@NotNull ChargingEquipmentError> getErrorModes() { return Modes.getActiveModes(ChargingEquipmentError.class, getChargingEquipmentStatus()); }
	/**
	 * @deprecated Use {@link #getChargingEquipmentStatus()} instead
	 * @return {@link #getChargingEquipmentStatus()}
	 */
	@Deprecated
	@Override
	default int getErrorModeValue() { return getChargingEquipmentStatus(); }
	@GraphQLInclude("isRunning")
	default boolean isRunning() { return (getChargingEquipmentStatus() & 1) == 1; }
	@GraphQLInclude("inputVoltageStatus")
	default @NotNull InputVoltageStatus getInputVoltageStatus() { return Modes.getActiveMode(InputVoltageStatus.class, getInputVoltageStatusValue()); }
	@GraphQLInclude("chargingStatus")
	default @NotNull ChargingStatus getChargingStatus() { return Modes.getActiveMode(ChargingStatus.class, getChargingStatusValue()); }
	@GraphQLInclude("chargingStatusName")
	default @NotNull String getChargingStatusName() { return getChargingStatus().getModeName(); }

	@Override
	default boolean hasError() {
		Set<ChargingEquipmentError> errors = getErrorModes();
		if (errors.contains(ChargingEquipmentError.FAULT)) {
			// fault seems to always be present, so we'll ignore it for now.
			//   Consider removing fault or something.
			return errors.size() > 1;
		}
		return !errors.isEmpty();
	}
}

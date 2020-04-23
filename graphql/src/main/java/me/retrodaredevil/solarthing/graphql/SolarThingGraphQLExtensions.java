package me.retrodaredevil.solarthing.graphql;

import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLQuery;
import me.retrodaredevil.solarthing.solar.outback.fx.ACMode;
import me.retrodaredevil.solarthing.solar.outback.fx.OperationalMode;
import me.retrodaredevil.solarthing.solar.outback.fx.event.FXACModeChangePacket;
import me.retrodaredevil.solarthing.solar.outback.fx.event.FXOperationalModeChangePacket;
import me.retrodaredevil.solarthing.solar.outback.mx.ChargerMode;
import me.retrodaredevil.solarthing.solar.outback.mx.event.MXChargerModeChangePacket;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverStatusPacket;
import org.jetbrains.annotations.Nullable;

import javax.validation.constraints.NotNull;

public class SolarThingGraphQLExtensions {
	@GraphQLQuery(name = "controllerTemperatureFahrenheit")
	public float getControllerTemperatureFahrenheit(@GraphQLContext RoverStatusPacket roverStatusPacket){
		return roverStatusPacket.getControllerTemperatureCelsius() * 9 / 5.0f + 32;
	}
	@GraphQLQuery(name = "batteryTemperatureFahrenheit")
	public float getBatteryTemperatureFahrenheit(@GraphQLContext RoverStatusPacket roverStatusPacket){
		return roverStatusPacket.getBatteryTemperatureCelsius() * 9 / 5.0f + 32;
	}

	@GraphQLQuery(name = "acModeName")
	public @NotNull String getACModeName(@GraphQLContext FXACModeChangePacket fxACModeChangePacket) {
		return fxACModeChangePacket.getACMode().getModeName();
	}
	@GraphQLQuery(name = "previousACModeName")
	public @Nullable String getPreviousACModeName(@GraphQLContext FXACModeChangePacket fxACModeChangePacket) {
		ACMode acMode = fxACModeChangePacket.getPreviousACMode();
		return acMode == null ? null : acMode.getModeName();
	}
	@GraphQLQuery(name = "operationalModeName")
	public @NotNull String getOperationalModeName(@GraphQLContext FXOperationalModeChangePacket fxOperationalModeChangePacket) {
		return fxOperationalModeChangePacket.getOperationalMode().getModeName();
	}
	@GraphQLQuery(name = "previousOperationalModeName")
	public @Nullable String getPreviousOperationalModeName(@GraphQLContext FXOperationalModeChangePacket fxOperationalModeChangePacket) {
		OperationalMode operationalMode = fxOperationalModeChangePacket.getPreviousOperationalMode();
		return operationalMode == null ? null : operationalMode.getModeName();
	}
	@GraphQLQuery(name = "chargingModeName")
	public @NotNull String getChargingModeName(@GraphQLContext MXChargerModeChangePacket mxChargerModeChangePacket) {
		return mxChargerModeChangePacket.getChargingMode().getModeName();
	}
	@GraphQLQuery(name = "previousChargingModeName")
	public @Nullable String getPreviousChargingModeName(@GraphQLContext MXChargerModeChangePacket mxChargerModeChangePacket) {
		ChargerMode previousChargerMode = mxChargerModeChangePacket.getPreviousChargingMode();
		return previousChargerMode == null ? null : previousChargerMode.getModeName();
	}
}

package me.retrodaredevil.solarthing.rest.graphql;

import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLQuery;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.misc.common.DataIdentifiable;
import me.retrodaredevil.solarthing.solar.outback.command.packets.SuccessMateCommandPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.ACMode;
import me.retrodaredevil.solarthing.solar.outback.fx.OperationalMode;
import me.retrodaredevil.solarthing.solar.outback.fx.event.FXACModeChangePacket;
import me.retrodaredevil.solarthing.solar.outback.fx.event.FXOperationalModeChangePacket;
import me.retrodaredevil.solarthing.solar.outback.mx.ChargerMode;
import me.retrodaredevil.solarthing.solar.outback.mx.event.MXChargerModeChangePacket;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverStatusPacket;
import me.retrodaredevil.solarthing.solar.tracer.TracerStatusPacket;

public class SolarThingGraphQLExtensions {
	@GraphQLQuery(name = "controllerTemperatureFahrenheit")
	public float getControllerTemperatureFahrenheit(@GraphQLContext RoverStatusPacket roverStatusPacket){
		return roverStatusPacket.getControllerTemperatureCelsius() * 9 / 5.0f + 32;
	}
	@GraphQLQuery(name = "batteryTemperatureFahrenheit")
	public float getBatteryTemperatureFahrenheit(@GraphQLContext RoverStatusPacket roverStatusPacket){
		return roverStatusPacket.getBatteryTemperatureCelsius() * 9 / 5.0f + 32;
	}
	@GraphQLQuery(name = "insideControllerTemperatureFahrenheit")
	public float getControllerTemperatureFahrenheit(@GraphQLContext TracerStatusPacket packet){
		return packet.getInsideControllerTemperatureCelsius() * 9 / 5.0f + 32;
	}
	@GraphQLQuery(name = "batteryTemperatureFahrenheit")
	public float getBatteryTemperatureFahrenheit(@GraphQLContext TracerStatusPacket packet){
		return packet.getBatteryTemperatureCelsius() * 9 / 5.0f + 32;
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

	@GraphQLQuery(name = "commandName")
	public @NotNull String getCommandName(@GraphQLContext SuccessMateCommandPacket successMateCommandPacket) {
		return successMateCommandPacket.getCommand().getCommandName();
	}

	@GraphQLQuery(name = "dataIdString")
	public @NotNull String getDataIdString(@GraphQLContext DataIdentifiable identifiable) {
		return "" + identifiable.getDataId();
	}
}

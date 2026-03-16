package me.retrodaredevil.solarthing.rest.graphql;

import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLQuery;
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
import me.retrodaredevil.solarthing.type.alter.flag.ActivePeriod;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.time.Instant;

@NullMarked
public class SolarThingGraphQLExtensions {
	@GraphQLQuery(name = "batteryTemperatureFahrenheit")
	public float getBatteryTemperatureFahrenheit(@GraphQLContext RoverStatusPacket roverStatusPacket){
		return roverStatusPacket.getBatteryTemperatureCelsius() * 9 / 5.0f + 32;
	}
	@Deprecated
	@GraphQLQuery(name = "insideControllerTemperatureFahrenheit")
	public float getControllerTemperatureFahrenheit(@GraphQLContext TracerStatusPacket packet){
		return packet.getInsideControllerTemperatureCelsius() * 9 / 5.0f + 32;
	}

	@GraphQLQuery(name = "acModeName")
	public @NonNull String getACModeName(@GraphQLContext FXACModeChangePacket fxACModeChangePacket) {
		return fxACModeChangePacket.getACMode().getModeName();
	}
	@GraphQLQuery(name = "previousACModeName")
	public @Nullable String getPreviousACModeName(@GraphQLContext FXACModeChangePacket fxACModeChangePacket) {
		ACMode acMode = fxACModeChangePacket.getPreviousACMode();
		return acMode == null ? null : acMode.getModeName();
	}
	@GraphQLQuery(name = "operationalModeName")
	public @NonNull String getOperationalModeName(@GraphQLContext FXOperationalModeChangePacket fxOperationalModeChangePacket) {
		return fxOperationalModeChangePacket.getOperationalMode().getModeName();
	}
	@GraphQLQuery(name = "previousOperationalModeName")
	public @Nullable String getPreviousOperationalModeName(@GraphQLContext FXOperationalModeChangePacket fxOperationalModeChangePacket) {
		OperationalMode operationalMode = fxOperationalModeChangePacket.getPreviousOperationalMode();
		return operationalMode == null ? null : operationalMode.getModeName();
	}
	@GraphQLQuery(name = "chargingModeName")
	public @NonNull String getChargingModeName(@GraphQLContext MXChargerModeChangePacket mxChargerModeChangePacket) {
		return mxChargerModeChangePacket.getChargingMode().getModeName();
	}
	@GraphQLQuery(name = "previousChargingModeName")
	public @Nullable String getPreviousChargingModeName(@GraphQLContext MXChargerModeChangePacket mxChargerModeChangePacket) {
		ChargerMode previousChargerMode = mxChargerModeChangePacket.getPreviousChargingMode();
		return previousChargerMode == null ? null : previousChargerMode.getModeName();
	}

	@GraphQLQuery(name = "commandName")
	public @NonNull String getCommandName(@GraphQLContext SuccessMateCommandPacket successMateCommandPacket) {
		return successMateCommandPacket.getCommand().getCommandName();
	}

	@GraphQLQuery(name = "dataIdString")
	public @NonNull String getDataIdString(@GraphQLContext DataIdentifiable identifiable) {
		return "" + identifiable.getDataId();
	}
	@GraphQLQuery
	public boolean isActive(@GraphQLContext ActivePeriod activePeriod, @GraphQLArgument(name = "dateMillis") long dateMillis) {
		return activePeriod.isActive(dateMillis);
	}
	@GraphQLQuery
	public boolean isActiveNow(@GraphQLContext ActivePeriod activePeriod) {
		return activePeriod.isActive(Instant.now());
	}
}

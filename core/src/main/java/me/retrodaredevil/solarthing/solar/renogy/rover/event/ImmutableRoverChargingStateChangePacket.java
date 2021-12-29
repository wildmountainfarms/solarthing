package me.retrodaredevil.solarthing.solar.renogy.rover.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.packets.identification.DefaultSupplementaryIdentifier;
import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;
import me.retrodaredevil.solarthing.packets.identification.KnownSupplementaryIdentifier;
import me.retrodaredevil.solarthing.solar.event.SolarEventPacketType;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverIdentifier;
import me.retrodaredevil.solarthing.solar.renogy.rover.RoverIdentityInfo;

public class ImmutableRoverChargingStateChangePacket implements RoverChargingStateChangePacket {
	private final int chargingStateValue;
	private final @Nullable Integer previousChargingStateValue;
	private final KnownSupplementaryIdentifier<RoverIdentifier> identifier;
	private final IdentityInfo identityInfo;

	@JsonCreator
	private ImmutableRoverChargingStateChangePacket(
			@JsonProperty(value = "number", required = true) int number,
			@JsonProperty(value = "chargingStateValue", required = true) int chargingStateValue,
			@JsonProperty(value = "previousChargingStateValue", required = true) @Nullable Integer previousChargingStateValue
	) {
		this(RoverIdentifier.getFromNumber(number), chargingStateValue, previousChargingStateValue);
	}

	public ImmutableRoverChargingStateChangePacket(RoverIdentifier roverIdentifier, int chargingStateValue, @Nullable Integer previousChargingStateValue) {
		this.chargingStateValue = chargingStateValue;
		this.previousChargingStateValue = previousChargingStateValue;

		identifier = new DefaultSupplementaryIdentifier<>(roverIdentifier, SolarEventPacketType.ROVER_CHARGING_STATE_CHANGE.toString());
		identityInfo = new RoverIdentityInfo(null, null);
	}

	@Override
	public @NotNull IdentityInfo getIdentityInfo() {
		return identityInfo;
	}

	@Override
	public int getNumber() {
		return identifier.getSupplementaryTo().getNumber();
	}

	@Override
	public @NotNull KnownSupplementaryIdentifier<RoverIdentifier> getIdentifier() {
		return identifier;
	}

	@Override
	public int getChargingStateValue() {
		return chargingStateValue;
	}

	@Override
	public @Nullable Integer getPreviousChargingStateValue() {
		return previousChargingStateValue;
	}
}

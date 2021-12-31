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

public class ImmutableRoverErrorModeChangePacket implements RoverErrorModeChangePacket {
	private final int errorModeValue;
	private final @Nullable Integer previousErrorModeValue;
	private final KnownSupplementaryIdentifier<RoverIdentifier> identifier;
	private final IdentityInfo identityInfo;

	@JsonCreator
	private ImmutableRoverErrorModeChangePacket(
			@JsonProperty(value = "number", required = true) int number,
			@JsonProperty(value = "errorModeValue", required = true) int errorModeValue,
			@JsonProperty(value = "previousErrorModeValue", required = true) @Nullable Integer previousErrorModeValue
	) {
		this(RoverIdentifier.getFromNumber(number), errorModeValue, previousErrorModeValue);
	}

	public ImmutableRoverErrorModeChangePacket(RoverIdentifier roverIdentifier, int errorModeValue, @Nullable Integer previousErrorModeValue) {
		this.errorModeValue = errorModeValue;
		this.previousErrorModeValue = previousErrorModeValue;

		identifier = new DefaultSupplementaryIdentifier<>(roverIdentifier, SolarEventPacketType.ROVER_ERROR_MODE_CHANGE.toString());
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
	public int getErrorModeValue() {
		return errorModeValue;
	}

	@Override
	public @Nullable Integer getPreviousErrorModeValue() {
		return previousErrorModeValue;
	}
}

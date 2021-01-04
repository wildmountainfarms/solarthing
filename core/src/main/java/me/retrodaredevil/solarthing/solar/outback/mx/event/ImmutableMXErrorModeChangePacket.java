package me.retrodaredevil.solarthing.solar.outback.mx.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.packets.identification.DefaultSupplementaryIdentifier;
import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;
import me.retrodaredevil.solarthing.packets.identification.KnownSupplementaryIdentifier;
import me.retrodaredevil.solarthing.solar.event.SolarEventPacketType;
import me.retrodaredevil.solarthing.solar.outback.OutbackIdentifier;
import me.retrodaredevil.solarthing.solar.outback.mx.MXIdentityInfo;

public class ImmutableMXErrorModeChangePacket implements MXErrorModeChangePacket {
	private final int address;
	private final int errorModeValue;
	private final Integer previousErrorModeValue;
	private final KnownSupplementaryIdentifier<OutbackIdentifier> identifier;
	private final IdentityInfo identityInfo;
	@JsonCreator
	private ImmutableMXErrorModeChangePacket(
			@JsonProperty(value = "address", required = true) int address,
			@JsonProperty(value = "errorModeValue", required = true) int errorModeValue,
			@JsonProperty(value = "previousErrorModeValue", required = true) Integer previousErrorModeValue
	) {
		this(new OutbackIdentifier(address), errorModeValue, previousErrorModeValue);
	}

	public ImmutableMXErrorModeChangePacket(OutbackIdentifier outbackIdentifier, int errorModeValue, Integer previousErrorModeValue) {
		this.errorModeValue = errorModeValue;
		this.previousErrorModeValue = previousErrorModeValue;

		address = outbackIdentifier.getAddress();
		identifier = new DefaultSupplementaryIdentifier<>(outbackIdentifier, SolarEventPacketType.MXFM_ERROR_MODE_CHANGE.toString());
		identityInfo = new MXIdentityInfo(address);
	}

	@Override
	public int getErrorModeValue() {
		return errorModeValue;
	}

	@Override
	public @Nullable Integer getPreviousErrorModeValue() {
		return previousErrorModeValue;
	}

	@Override
	public @NotNull KnownSupplementaryIdentifier<OutbackIdentifier> getIdentifier() {
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
}

package me.retrodaredevil.solarthing.solar.outback.mx.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.packets.identification.DefaultSupplementaryIdentifier;
import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;
import me.retrodaredevil.solarthing.packets.identification.KnownSupplementaryIdentifier;
import me.retrodaredevil.solarthing.packets.identification.SupplementaryIdentifier;
import me.retrodaredevil.solarthing.solar.event.SolarEventPacketType;
import me.retrodaredevil.solarthing.solar.outback.OutbackIdentifier;
import me.retrodaredevil.solarthing.solar.outback.mx.MXIdentityInfo;

public class ImmutableMXAuxModeChangePacket implements MXAuxModeChangePacket {
	private final int address;
	private final int rawAuxModeValue;
	private final Integer previousRawAuxModeValue;
	private final KnownSupplementaryIdentifier<OutbackIdentifier> identifier;
	private final IdentityInfo identityInfo;
	@JsonCreator
	private ImmutableMXAuxModeChangePacket(
			@JsonProperty(value = "address", required = true) int address,
			@JsonProperty(value = "rawAuxModeValue", required = true) int rawAuxModeValue,
			@JsonProperty(value = "previousRawAuxModeValue", required = true) Integer previousRawAuxModeValue
	) {
		this(new OutbackIdentifier(address), rawAuxModeValue, previousRawAuxModeValue);
	}

	public ImmutableMXAuxModeChangePacket(OutbackIdentifier outbackIdentifier, int rawAuxModeValue, Integer previousRawAuxModeValue) {
		this.rawAuxModeValue = rawAuxModeValue;
		this.previousRawAuxModeValue = previousRawAuxModeValue;

		address = outbackIdentifier.getAddress();
		identifier = new DefaultSupplementaryIdentifier<>(outbackIdentifier, SolarEventPacketType.MXFM_AUX_MODE_CHANGE.toString());
		identityInfo = new MXIdentityInfo(address);
	}

	@Override
	public @NotNull IdentityInfo getIdentityInfo() {
		return identityInfo;
	}

	@Override
	public int getRawAuxModeValue() {
		return rawAuxModeValue;
	}

	@Override
	public @Nullable Integer getPreviousRawAuxModeValue() {
		return previousRawAuxModeValue;
	}

	@Override
	public @NotNull KnownSupplementaryIdentifier<OutbackIdentifier> getIdentifier() {
		return identifier;
	}

	@Override
	public int getAddress() {
		return address;
	}
}

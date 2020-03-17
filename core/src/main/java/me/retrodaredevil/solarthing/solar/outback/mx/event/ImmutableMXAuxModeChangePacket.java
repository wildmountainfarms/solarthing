package me.retrodaredevil.solarthing.solar.outback.mx.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.packets.identification.DefaultSupplementaryIdentifier;
import me.retrodaredevil.solarthing.packets.identification.SupplementaryIdentifier;
import me.retrodaredevil.solarthing.solar.event.SolarEventPacketType;
import me.retrodaredevil.solarthing.solar.outback.OutbackIdentifier;
import org.jetbrains.annotations.Nullable;

public class ImmutableMXAuxModeChangePacket implements MXAuxModeChangePacket {
	private final int address;
	private final int rawAuxModeValue;
	private final Integer previousRawAuxModeValue;
	private final SupplementaryIdentifier identifier;
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
	public SupplementaryIdentifier getIdentifier() {
		return identifier;
	}

	@Override
	public int getAddress() {
		return address;
	}
}

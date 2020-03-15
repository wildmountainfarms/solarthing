package me.retrodaredevil.solarthing.solar.outback.fx.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.packets.identification.DefaultSupplementaryIdentifier;
import me.retrodaredevil.solarthing.packets.identification.SupplementaryIdentifier;
import me.retrodaredevil.solarthing.solar.event.SolarEventPacketType;
import me.retrodaredevil.solarthing.solar.outback.OutbackIdentifier;
import org.jetbrains.annotations.Nullable;

public class ImmutableFXACModeChangePacket implements FXACModeChangePacket {
	private final int address;
	private final int acModeValue;
	private final Integer previousACModeValue;

	private final SupplementaryIdentifier identifier;

	@JsonCreator
	private ImmutableFXACModeChangePacket(
			@JsonProperty(value = "address", required = true) int address,
			@JsonProperty(value = "acModeValue", required = true) int acModeValue,
			@JsonProperty(value = "previousACModeValue", required = true) Integer previousACModeValue) {
		this(new OutbackIdentifier(address), acModeValue, previousACModeValue);
	}

	public ImmutableFXACModeChangePacket(OutbackIdentifier outbackIdentifier, int acModeValue, Integer previousACModeValue) {
		this.acModeValue = acModeValue;
		this.previousACModeValue = previousACModeValue;

		this.address = outbackIdentifier.getAddress();
		this.identifier = new DefaultSupplementaryIdentifier<>(outbackIdentifier, SolarEventPacketType.FX_AC_MODE_CHANGE.toString());
	}

	@Override
	public int getAddress() {
		return address;
	}

	@Override
	public int getACModeValue() {
		return acModeValue;
	}

	@Override
	public @Nullable Integer getPreviousACModeValue() {
		return previousACModeValue;
	}

	@Override
	public SupplementaryIdentifier getIdentifier() {
		return identifier;
	}
}

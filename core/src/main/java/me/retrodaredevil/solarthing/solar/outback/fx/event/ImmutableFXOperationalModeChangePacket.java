package me.retrodaredevil.solarthing.solar.outback.fx.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.packets.identification.DefaultSupplementaryIdentifier;
import me.retrodaredevil.solarthing.packets.identification.SupplementaryIdentifier;
import me.retrodaredevil.solarthing.solar.event.SolarEventPacketType;
import me.retrodaredevil.solarthing.solar.outback.OutbackIdentifier;
import org.jetbrains.annotations.Nullable;

import javax.validation.constraints.NotNull;

public class ImmutableFXOperationalModeChangePacket implements FXOperationalModeChangePacket {
	private final int address;
	private final int operationalModeValue;
	private final Integer previousOperationalModeValue;
	private final SupplementaryIdentifier identifier;

	@JsonCreator
	private ImmutableFXOperationalModeChangePacket(
			@JsonProperty(value = "address", required = true) int address,
			@JsonProperty(value = "operationalModeValue", required = true) int operationalModeValue,
			@JsonProperty(value = "previousOperationalModeValue", required = true) Integer previousOperationalModeValue
	) {
		this(new OutbackIdentifier(address), operationalModeValue, previousOperationalModeValue);
	}
	public ImmutableFXOperationalModeChangePacket(
			OutbackIdentifier outbackIdentifier,
			int operationalModeValue,
			Integer previousOperationalModeValue
	) {
		this.operationalModeValue = operationalModeValue;
		this.previousOperationalModeValue = previousOperationalModeValue;

		address = outbackIdentifier.getAddress();
		identifier = new DefaultSupplementaryIdentifier<>(outbackIdentifier, SolarEventPacketType.FX_OPERATIONAL_MODE_CHANGE.toString());
	}

	@Override
	public int getAddress() {
		return address;
	}

	@Override
	public int getOperationalModeValue() {
		return operationalModeValue;
	}

	@Override
	public @Nullable Integer getPreviousOperationalModeValue() {
		return previousOperationalModeValue;
	}

	@NotNull
    @Override
	public SupplementaryIdentifier getIdentifier() {
		return identifier;
	}
}

package me.retrodaredevil.solarthing.solar.outback.fx.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.identification.DefaultSupplementaryIdentifier;
import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;
import me.retrodaredevil.solarthing.packets.identification.KnownSupplementaryIdentifier;
import me.retrodaredevil.solarthing.packets.identification.SupplementaryIdentifier;
import me.retrodaredevil.solarthing.solar.event.SolarEventPacketType;
import me.retrodaredevil.solarthing.solar.outback.OutbackIdentifier;
import me.retrodaredevil.solarthing.solar.outback.fx.FXIdentityInfo;

public class ImmutableFXWarningModeChangePacket implements FXWarningModeChangePacket {
	private final int address;
	private final int warningModeValue;
	private final Integer previousWarningModeValue;
	private final int ignoredWarningModeValueConstant;
	private final KnownSupplementaryIdentifier<OutbackIdentifier> identifier;
	private final IdentityInfo identityInfo;
	@JsonCreator
	private ImmutableFXWarningModeChangePacket(
			@JsonProperty(value = "address", required = true) int address,
			@JsonProperty(value = "warningModeValue", required = true) int warningModeValue,
			@JsonProperty(value = "previousWarningModeValue", required = true) Integer previousWarningModeValue,
			@JsonProperty(value = "ignoredWarningModeValueConstant", required = true) int ignoredWarningModeValueConstant
	) {
		this(new OutbackIdentifier(address), warningModeValue, previousWarningModeValue, ignoredWarningModeValueConstant);
	}

	public ImmutableFXWarningModeChangePacket(OutbackIdentifier outbackIdentifier, int warningModeValue, Integer previousWarningModeValue, int ignoredWarningModeValueConstant) {
		this.warningModeValue = warningModeValue;
		this.previousWarningModeValue = previousWarningModeValue;
		this.ignoredWarningModeValueConstant = ignoredWarningModeValueConstant;

		address = outbackIdentifier.getAddress();
		identifier = new DefaultSupplementaryIdentifier<>(outbackIdentifier, SolarEventPacketType.FX_WARNING_MODE_CHANGE.toString());
		identityInfo = new FXIdentityInfo(address);
	}

	@Override
	public int getWarningModeValue() {
		return warningModeValue;
	}

	@Override
	public Integer getPreviousWarningModeValue() {
		return previousWarningModeValue;
	}

	@Override
	public int getIgnoredWarningModeValueConstant() {
		return ignoredWarningModeValueConstant;
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

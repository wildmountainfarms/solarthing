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

public class ImmutableMXChargerModeChangePacket implements MXChargerModeChangePacket {
	private final int address;
	private final int chargerModeValue;
	private final Integer previousChargerModeValue;
	private final KnownSupplementaryIdentifier<OutbackIdentifier> identifier;
	private final IdentityInfo identityInfo;

	@JsonCreator
	private ImmutableMXChargerModeChangePacket(
			@JsonProperty(value = "address", required = true) int address,
			@JsonProperty(value = "chargerModeValue", required = true) int chargerModeValue,
			@JsonProperty(value = "previousChargerModeValue", required = true) Integer previousChargerModeValue
	) {
		this(new OutbackIdentifier(address), chargerModeValue, previousChargerModeValue);
	}

	public ImmutableMXChargerModeChangePacket(OutbackIdentifier outbackIdentifier, int chargerModeValue, Integer previousChargerModeValue) {
		this.chargerModeValue = chargerModeValue;
		this.previousChargerModeValue = previousChargerModeValue;

		address = outbackIdentifier.getAddress();
		identifier = new DefaultSupplementaryIdentifier<>(outbackIdentifier, SolarEventPacketType.MXFM_CHARGER_MODE_CHANGE.toString());
		identityInfo = new MXIdentityInfo(address);
	}

	@Override
	public int getChargerModeValue() {
		return chargerModeValue;
	}

	@Override
	public @Nullable Integer getPreviousChargerModeValue() {
		return previousChargerModeValue;
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

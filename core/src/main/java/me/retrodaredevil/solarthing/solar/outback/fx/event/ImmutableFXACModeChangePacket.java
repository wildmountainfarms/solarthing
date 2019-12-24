package me.retrodaredevil.solarthing.solar.outback.fx.event;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import me.retrodaredevil.solarthing.packets.identification.DefaultSupplementaryIdentifier;
import me.retrodaredevil.solarthing.packets.identification.SupplementaryIdentifier;
import me.retrodaredevil.solarthing.solar.event.SolarEventPacketType;
import me.retrodaredevil.solarthing.solar.outback.OutbackIdentifier;
import org.jetbrains.annotations.Nullable;

import java.beans.ConstructorProperties;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class ImmutableFXACModeChangePacket implements FXACModeChangePacket {
	private final SolarEventPacketType packetType = SolarEventPacketType.FX_AC_MODE_CHANGE;
	private final int address;
	private final int acModeValue;
	private final Integer previousACModeValue;

	private final transient SupplementaryIdentifier identifier;

	@ConstructorProperties({"acModeValue", "previousACModeValue", "address"})
	private ImmutableFXACModeChangePacket(int acModeValue, Integer previousACModeValue, int address) {
		this(acModeValue, previousACModeValue, new OutbackIdentifier(address));
	}

	public ImmutableFXACModeChangePacket(int acModeValue, Integer previousACModeValue, OutbackIdentifier outbackIdentifier) {
		this.acModeValue = acModeValue;
		this.previousACModeValue = previousACModeValue;

		this.address = outbackIdentifier.getAddress();
		this.identifier = new DefaultSupplementaryIdentifier<>(outbackIdentifier, SolarEventPacketType.FX_AC_MODE_CHANGE.toString());
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
	public SolarEventPacketType getPacketType() {
		return packetType;
	}

	@Override
	public SupplementaryIdentifier getIdentifier() {
		return identifier;
	}
}

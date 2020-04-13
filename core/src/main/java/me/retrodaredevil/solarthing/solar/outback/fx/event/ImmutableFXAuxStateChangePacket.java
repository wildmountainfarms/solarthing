package me.retrodaredevil.solarthing.solar.outback.fx.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.packets.identification.DefaultSupplementaryIdentifier;
import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;
import me.retrodaredevil.solarthing.packets.identification.SupplementaryIdentifier;
import me.retrodaredevil.solarthing.solar.event.SolarEventPacketType;
import me.retrodaredevil.solarthing.solar.outback.OutbackIdentifier;
import me.retrodaredevil.solarthing.solar.outback.fx.FXIdentityInfo;
import org.jetbrains.annotations.Nullable;

import javax.validation.constraints.NotNull;

public class ImmutableFXAuxStateChangePacket implements FXAuxStateChangePacket {
	private final int address;
	private final boolean isAuxActive;
	private final Boolean wasAuxActive;

	private final SupplementaryIdentifier identifier;
	private final IdentityInfo identityInfo;

	@JsonCreator
	private ImmutableFXAuxStateChangePacket(
			@JsonProperty(value = "address", required = true) int address,
			@JsonProperty(value = "isAuxActive", required = true) boolean isAuxActive,
			@JsonProperty(value = "wasAuxActive", required = true) Boolean wasAuxActive
	) {
		this(new OutbackIdentifier(address), isAuxActive, wasAuxActive);
	}
	public ImmutableFXAuxStateChangePacket(
			OutbackIdentifier outbackIdentifier,
			boolean isAuxActive,
			Boolean wasAuxActive
	) {
		this.isAuxActive = isAuxActive;
		this.wasAuxActive = wasAuxActive;
		if(wasAuxActive != null && isAuxActive == wasAuxActive){
			throw new IllegalArgumentException("isAuxActive must be different than wasAuxActive! isAuxActive=" + isAuxActive + " wasAuxActive=" + wasAuxActive);
		}

		address = outbackIdentifier.getAddress();
		identifier = new DefaultSupplementaryIdentifier<>(outbackIdentifier, SolarEventPacketType.FX_AUX_STATE_CHANGE.toString());
		identityInfo = new FXIdentityInfo(address);
	}

	@Override
	public boolean isAuxActive() {
		return isAuxActive;
	}

	@Override
	public @Nullable Boolean getAuxWasActive() {
		return wasAuxActive;
	}

	@NotNull
    @Override
	public SupplementaryIdentifier getIdentifier() {
		return identifier;
	}

	@NotNull
	@Override
	public IdentityInfo getIdentityInfo() {
		return identityInfo;
	}

	@Override
	public int getAddress() {
		return address;
	}
}

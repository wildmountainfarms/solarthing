package me.retrodaredevil.solarthing.solar.outback.mx.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.DefaultFinal;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.packets.ChangePacket;
import me.retrodaredevil.solarthing.packets.Modes;
import me.retrodaredevil.solarthing.solar.event.SolarEventPacketType;
import me.retrodaredevil.solarthing.solar.event.SupplementarySolarEventPacket;
import me.retrodaredevil.solarthing.solar.outback.OutbackData;
import me.retrodaredevil.solarthing.solar.outback.SupplementaryOutbackPacket;
import me.retrodaredevil.solarthing.solar.outback.mx.AuxMode;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;

@JsonDeserialize(as = ImmutableMXAuxModeChangePacket.class)
@JsonTypeName("MXFM_AUX_MODE_CHANGE")
@JsonExplicit
public interface MXAuxModeChangePacket extends SupplementarySolarEventPacket, SupplementaryOutbackPacket, OutbackData, ChangePacket {
	@DefaultFinal
	@Override
	default @NotNull SolarEventPacketType getPacketType(){
		return SolarEventPacketType.MXFM_AUX_MODE_CHANGE;
	}

	@JsonProperty("rawAuxModeValue")
	int getRawAuxModeValue();
	@JsonProperty("previousRawAuxModeValue")
	@Nullable Integer getPreviousRawAuxModeValue();

	default int getAuxModeValue() {
		return AuxMode.getActualValueCode(getRawAuxModeValue());
	}
	default @NotNull AuxMode getAuxMode(){ return Modes.getActiveMode(AuxMode.class, getAuxModeValue());}
	default boolean isAuxBitActive(){ return AuxMode.isAuxModeActive(getRawAuxModeValue()); }

	default @Nullable Integer getPreviousAuxModeValue(){
		Integer raw = getPreviousRawAuxModeValue();
		if(raw == null){
			return null;
		}
		return AuxMode.getActualValueCode(raw);
	}
	default @Nullable AuxMode getPreviousAuxMode(){
		Integer value = getPreviousAuxModeValue();
		if(value == null){
			return null;
		}
		return Modes.getActiveMode(AuxMode.class, value);
	}
	default @Nullable Boolean getWasAuxBitActive(){
		Integer raw = getPreviousRawAuxModeValue();
		if(raw == null){
			return null;
		}
		return AuxMode.isAuxModeActive(raw);
	}

	@Override
	default boolean isLastUnknown() {
		return getPreviousRawAuxModeValue() == null;
	}
}

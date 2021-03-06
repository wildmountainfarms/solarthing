package me.retrodaredevil.solarthing.solar.outback.mx.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.DefaultFinal;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.packets.ChangePacket;
import me.retrodaredevil.solarthing.packets.Modes;
import me.retrodaredevil.solarthing.solar.event.SolarEventPacketType;
import me.retrodaredevil.solarthing.solar.event.SupplementarySolarEventPacket;
import me.retrodaredevil.solarthing.solar.outback.SupplementaryOutbackPacket;
import me.retrodaredevil.solarthing.solar.outback.mx.ChargerMode;

@JsonDeserialize(as = ImmutableMXChargerModeChangePacket.class)
@JsonTypeName("MXFM_CHARGER_MODE_CHANGE")
@JsonExplicit
public interface MXChargerModeChangePacket extends SupplementarySolarEventPacket, SupplementaryOutbackPacket, ChangePacket {
	@DefaultFinal
	@Override
	default @NotNull SolarEventPacketType getPacketType(){
		return SolarEventPacketType.MXFM_CHARGER_MODE_CHANGE;
	}

	@JsonProperty("chargerModeValue")
	int getChargerModeValue();
	@JsonProperty("previousChargerModeValue")
	@Nullable Integer getPreviousChargerModeValue();

	default ChargerMode getChargingMode(){
		return Modes.getActiveMode(ChargerMode.class, getChargerModeValue());
	}
	default @Nullable ChargerMode getPreviousChargingMode(){
		Integer previousChargerModeValue = getPreviousChargerModeValue();
		if(previousChargerModeValue == null){
			return null;
		}
		return Modes.getActiveMode(ChargerMode.class, previousChargerModeValue);
	}

	@Override
	default boolean isLastUnknown() {
		return getPreviousChargerModeValue() == null;
	}
}

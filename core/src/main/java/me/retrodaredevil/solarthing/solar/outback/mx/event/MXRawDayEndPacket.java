package me.retrodaredevil.solarthing.solar.outback.mx.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.packets.support.Support;
import me.retrodaredevil.solarthing.solar.common.AccumulatedChargeController;
import me.retrodaredevil.solarthing.solar.event.SolarEventPacketType;
import me.retrodaredevil.solarthing.solar.event.SupplementarySolarEventPacket;
import me.retrodaredevil.solarthing.solar.outback.OutbackData;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.solar.outback.SupplementaryOutbackPacket;

@JsonTypeName("MXFM_RAW_DAY_END")
@JsonDeserialize(as = ImmutableMXRawDayEndPacket.class)
@JsonExplicit
public interface MXRawDayEndPacket extends SupplementarySolarEventPacket, AccumulatedChargeController, SupplementaryOutbackPacket {
	@NotNull
    @Override
	default SolarEventPacketType getPacketType(){
		return SolarEventPacketType.MXFM_RAW_DAY_END;
	}

	@NotNull
	@JsonProperty("dailyAHSupport")
	@Override
	Support getDailyAHSupport();

}

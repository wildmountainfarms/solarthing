package me.retrodaredevil.solarthing.solar.outback.mx.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.packets.support.Support;
import me.retrodaredevil.solarthing.solar.common.AccumulatedChargeController;
import me.retrodaredevil.solarthing.solar.event.SolarEventPacket;
import me.retrodaredevil.solarthing.solar.event.SolarEventPacketType;
import me.retrodaredevil.solarthing.solar.outback.OutbackData;

import javax.validation.constraints.NotNull;

@JsonTypeName("MXFM_RAW_DAY_END")
@JsonDeserialize(as = ImmutableMXRawDayEndPacket.class)
@JsonExplicit
public interface MXRawDayEndPacket extends SolarEventPacket, AccumulatedChargeController, OutbackData {
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

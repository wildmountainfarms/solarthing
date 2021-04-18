package me.retrodaredevil.solarthing.solar.outback.mx.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.DefaultFinal;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.support.Support;
import me.retrodaredevil.solarthing.solar.common.AccumulatedChargeController;
import me.retrodaredevil.solarthing.solar.common.DailyData;
import me.retrodaredevil.solarthing.solar.event.SolarEventPacketType;
import me.retrodaredevil.solarthing.solar.event.SupplementarySolarEventPacket;
import me.retrodaredevil.solarthing.solar.outback.SupplementaryOutbackPacket;

@JsonTypeName("MXFM_RAW_DAY_END")
@JsonDeserialize(as = ImmutableMXRawDayEndPacket.class)
@JsonExplicit
public interface MXRawDayEndPacket extends SupplementarySolarEventPacket, AccumulatedChargeController, SupplementaryOutbackPacket {
	@DefaultFinal
	@Override
	default @NotNull SolarEventPacketType getPacketType(){
		return SolarEventPacketType.MXFM_RAW_DAY_END;
	}

	@JsonProperty("dailyAHSupport")
	@Override
	@NotNull Support getDailyAHSupport();

	@Override
	default boolean isNewDay(DailyData previousDailyData) {
		// We have to override this since we inherit from DailyData
		return true; // Since this is an event packet, each one is always a new day
	}
}

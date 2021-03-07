package me.retrodaredevil.solarthing.solar.outback.fx.extra;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.DefaultFinal;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.solar.extra.SolarExtraPacketType;
import me.retrodaredevil.solarthing.solar.extra.SupplementarySolarExtraPacket;
import me.retrodaredevil.solarthing.solar.outback.SupplementaryOutbackPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.common.FXDailyData;

@JsonDeserialize(using = ImmutableDailyFXPacket.Deserializer.class)
@JsonTypeName("FX_DAILY")
public interface DailyFXPacket extends FXDailyData, SupplementarySolarExtraPacket, SupplementaryOutbackPacket {

	@DefaultFinal
	@Override
	default @NotNull SolarExtraPacketType getPacketType() {
		return SolarExtraPacketType.FX_DAILY;
	}
}

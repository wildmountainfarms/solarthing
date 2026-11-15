package me.retrodaredevil.solarthing.solar.outback.fx.extra;

import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.annotations.DefaultFinal;
import me.retrodaredevil.solarthing.solar.extra.SolarExtraPacketType;
import me.retrodaredevil.solarthing.solar.extra.SupplementarySolarExtraPacket;
import me.retrodaredevil.solarthing.solar.outback.SupplementaryOutbackPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.common.FXDailyData;
import org.jspecify.annotations.NullMarked;
import tools.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = ImmutableDailyFXPacket.Deserializer.class)
@JsonTypeName("FX_DAILY")
@NullMarked
public interface DailyFXPacket extends FXDailyData, SupplementarySolarExtraPacket, SupplementaryOutbackPacket {

	@DefaultFinal
	@Override
	default SolarExtraPacketType getPacketType() {
		return SolarExtraPacketType.FX_DAILY;
	}
}

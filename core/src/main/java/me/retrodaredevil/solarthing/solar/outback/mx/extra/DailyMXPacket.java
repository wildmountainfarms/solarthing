package me.retrodaredevil.solarthing.solar.outback.mx.extra;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.solar.extra.SolarExtraPacketType;
import me.retrodaredevil.solarthing.solar.extra.SupplementarySolarExtraPacket;
import me.retrodaredevil.solarthing.solar.outback.mx.common.MXDailyData;

import javax.validation.constraints.NotNull;

@JsonTypeName("MXFM_DAILY")
@JsonDeserialize(as = ImmutableDailyMXPacket.class)
public interface DailyMXPacket extends SupplementarySolarExtraPacket, MXDailyData {
	@NotNull
	@Override
	default SolarExtraPacketType getPacketType() {
		return SolarExtraPacketType.MXFM_DAILY;
	}

	@Deprecated
	@Override
	float getDailyKWH();

	@Deprecated
	@Override
	int getDailyAH();
}

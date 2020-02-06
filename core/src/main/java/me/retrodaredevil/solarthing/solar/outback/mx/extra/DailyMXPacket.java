package me.retrodaredevil.solarthing.solar.outback.mx.extra;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.solar.extra.SolarExtraPacket;
import me.retrodaredevil.solarthing.solar.extra.SupplementarySolarExtraPacket;
import me.retrodaredevil.solarthing.solar.outback.mx.common.MXDailyData;

@JsonTypeName("MXFM_DAILY")
@JsonDeserialize(as = ImmutableDailyMXPacket.class)
public interface DailyMXPacket extends SupplementarySolarExtraPacket, MXDailyData {
}

package me.retrodaredevil.solarthing.solar.outback.mx.event;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.solar.event.SupplementarySolarEventPacket;
import me.retrodaredevil.solarthing.solar.outback.SupplementaryOutbackPacket;
import me.retrodaredevil.solarthing.solar.outback.mx.common.MXDailyData;

@JsonDeserialize(as = ImmutableMXDayEndPacket.class)
@JsonTypeName("MXFM_DAILY_DAY_END")
@Deprecated
public interface MXDayEndPacket extends SupplementarySolarEventPacket, SupplementaryOutbackPacket, MXDailyData {
}

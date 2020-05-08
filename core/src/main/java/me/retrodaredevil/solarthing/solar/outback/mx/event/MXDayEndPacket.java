package me.retrodaredevil.solarthing.solar.outback.mx.event;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.identification.KnownSupplementaryIdentifier;
import me.retrodaredevil.solarthing.packets.identification.SupplementaryIdentifier;
import me.retrodaredevil.solarthing.solar.event.SupplementarySolarEventPacket;
import me.retrodaredevil.solarthing.solar.outback.OutbackIdentifier;
import me.retrodaredevil.solarthing.solar.outback.mx.common.MXDailyData;

@JsonDeserialize(as = ImmutableMXDayEndPacket.class)
@JsonTypeName("MXFM_DAILY_DAY_END")
public interface MXDayEndPacket extends SupplementarySolarEventPacket, MXDailyData {
	@Override
	@NotNull KnownSupplementaryIdentifier<OutbackIdentifier> getIdentifier();
}

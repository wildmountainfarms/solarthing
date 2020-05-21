package me.retrodaredevil.solarthing.solar.outback.mx.extra;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.packets.identification.KnownSupplementaryIdentifier;
import me.retrodaredevil.solarthing.packets.identification.SupplementaryIdentifier;
import me.retrodaredevil.solarthing.solar.extra.SolarExtraPacketType;
import me.retrodaredevil.solarthing.solar.extra.SupplementarySolarExtraPacket;
import me.retrodaredevil.solarthing.solar.outback.OutbackIdentifier;
import me.retrodaredevil.solarthing.solar.outback.SupplementaryOutbackPacket;
import me.retrodaredevil.solarthing.solar.outback.mx.common.MXDailyData;

import me.retrodaredevil.solarthing.annotations.NotNull;

@JsonTypeName("MXFM_DAILY")
@JsonDeserialize(as = ImmutableDailyMXPacket.class)
public interface DailyMXPacket extends SupplementarySolarExtraPacket, MXDailyData, SupplementaryOutbackPacket {
	@NotNull
	@Override
	default SolarExtraPacketType getPacketType() {
		return SolarExtraPacketType.MXFM_DAILY;
	}

	@Override
	@NotNull KnownSupplementaryIdentifier<OutbackIdentifier> getIdentifier();
}

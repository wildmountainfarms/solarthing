package me.retrodaredevil.solarthing.solar.outback.fx.extra;

import me.retrodaredevil.solarthing.solar.extra.SolarExtraPacketType;
import me.retrodaredevil.solarthing.solar.outback.OutbackIdentifier;
import me.retrodaredevil.solarthing.solar.outback.fx.common.BaseFXDailyData;
import me.retrodaredevil.solarthing.solar.outback.fx.common.FXDailyData;

import java.util.Collection;

public class ImmutableDailyFXPacket extends BaseFXDailyData implements DailyFXPacket {

	public ImmutableDailyFXPacket(FXDailyData fxDailyData, OutbackIdentifier outbackIdentifier) {
		super(SolarExtraPacketType.FX_DAILY, fxDailyData, outbackIdentifier);
	}

	@Override
	public SolarExtraPacketType getPacketType() {
		return SolarExtraPacketType.FX_DAILY;
	}
}

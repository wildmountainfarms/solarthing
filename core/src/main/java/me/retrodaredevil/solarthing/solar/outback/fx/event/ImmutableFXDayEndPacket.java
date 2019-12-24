package me.retrodaredevil.solarthing.solar.outback.fx.event;

import me.retrodaredevil.solarthing.solar.event.SolarEventPacketType;
import me.retrodaredevil.solarthing.solar.outback.OutbackIdentifier;
import me.retrodaredevil.solarthing.solar.outback.fx.common.BaseFXDailyData;
import me.retrodaredevil.solarthing.solar.outback.fx.common.FXDailyData;

public class ImmutableFXDayEndPacket extends BaseFXDailyData implements FXDayEndPacket {
	public ImmutableFXDayEndPacket(FXDailyData dailyData, OutbackIdentifier outbackIdentifier) {
		super(SolarEventPacketType.FX_DAILY_DAY_END, dailyData, outbackIdentifier);
	}

	@Override
	public SolarEventPacketType getPacketType() {
		return SolarEventPacketType.FX_DAILY_DAY_END;
	}
}

package me.retrodaredevil.solarthing.solar.outback.fx.extra;

import com.google.gson.JsonObject;
import me.retrodaredevil.solarthing.solar.outback.OutbackIdentifier;
import me.retrodaredevil.solarthing.solar.outback.fx.common.FXDailyData;
import me.retrodaredevil.solarthing.solar.outback.fx.common.FXDailyDataUtil;

public final class DailyFXPackets {
	private DailyFXPackets(){ throw new UnsupportedOperationException(); }

	public static DailyFXPacket createFromJson(JsonObject object){
		FXDailyData fxDailyData = FXDailyDataUtil.createFromJson(object);

		final int address = object.get("address").getAsInt();

		return new ImmutableDailyFXPacket(
				fxDailyData,
				new OutbackIdentifier(address)
		);
	}
}

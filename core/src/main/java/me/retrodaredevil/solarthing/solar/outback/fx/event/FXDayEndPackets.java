package me.retrodaredevil.solarthing.solar.outback.fx.event;

import com.google.gson.JsonObject;
import me.retrodaredevil.solarthing.solar.outback.OutbackIdentifier;
import me.retrodaredevil.solarthing.solar.outback.fx.common.FXDailyData;
import me.retrodaredevil.solarthing.solar.outback.fx.common.FXDailyDataUtil;

public final class FXDayEndPackets {
	private FXDayEndPackets(){ throw new UnsupportedOperationException(); }


	@Deprecated
	public static FXDayEndPacket createFromJson(JsonObject object){
		FXDailyData fxDailyData = FXDailyDataUtil.createFromJson(object);

		final int address = object.get("address").getAsInt();

		return new ImmutableFXDayEndPacket(
				fxDailyData,
				new OutbackIdentifier(address)
		);
	}
}

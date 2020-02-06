package me.retrodaredevil.solarthing.solar.outback.fx.event;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.solar.event.SupplementarySolarEventPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.common.FXDailyData;
import org.jetbrains.annotations.Nullable;

@JsonTypeName("FX_DAILY_DAY_END")
@JsonDeserialize(as = ImmutableFXDayEndPacket.class)
public interface FXDayEndPacket extends SupplementarySolarEventPacket, FXDailyData {
	@Override
	@Nullable Long getStartDateMillis();
}

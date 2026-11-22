package me.retrodaredevil.solarthing.solar.outback.fx.extra;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import me.retrodaredevil.solarthing.solar.extra.SolarExtraPacketType;
import me.retrodaredevil.solarthing.solar.outback.OutbackIdentifier;
import me.retrodaredevil.solarthing.solar.outback.fx.common.BaseFXDailyData;
import me.retrodaredevil.solarthing.solar.outback.fx.common.FXDailyData;
import me.retrodaredevil.solarthing.solar.outback.fx.common.ImmutableFXDailyData;
import org.jspecify.annotations.NullMarked;
import tools.jackson.databind.annotation.JsonDeserialize;

@NullMarked
public class ImmutableDailyFXPacket extends BaseFXDailyData implements DailyFXPacket {

	public ImmutableDailyFXPacket(FXDailyData fxDailyData, OutbackIdentifier outbackIdentifier) {
		super(SolarExtraPacketType.FX_DAILY, fxDailyData, outbackIdentifier);
	}

	@JsonCreator
	public ImmutableDailyFXPacket(
			@JsonUnwrapped
			@JsonProperty(required = true)
			@JsonDeserialize(as = ImmutableFXDailyData.class)
			FXDailyData fxDailyData
	) {
		this(fxDailyData, new OutbackIdentifier(fxDailyData.getAddress()));
	}
}

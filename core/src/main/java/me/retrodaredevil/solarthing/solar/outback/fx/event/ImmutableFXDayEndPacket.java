package me.retrodaredevil.solarthing.solar.outback.fx.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import me.retrodaredevil.solarthing.solar.event.SolarEventPacketType;
import me.retrodaredevil.solarthing.solar.outback.OutbackIdentifier;
import me.retrodaredevil.solarthing.solar.outback.fx.common.BaseFXDailyData;
import me.retrodaredevil.solarthing.solar.outback.fx.common.FXDailyData;
import me.retrodaredevil.solarthing.solar.outback.fx.common.ImmutableFXDailyData;
import me.retrodaredevil.solarthing.annotations.NotNull;

import static java.util.Objects.requireNonNull;

@JsonDeserialize(builder = ImmutableFXDayEndPacket.Builder.class)
public class ImmutableFXDayEndPacket extends BaseFXDailyData implements FXDayEndPacket {
	public ImmutableFXDayEndPacket(FXDailyData dailyData, OutbackIdentifier outbackIdentifier) {
		super(SolarEventPacketType.FX_DAILY_DAY_END, dailyData, outbackIdentifier);
		requireNonNull(dailyData.getStartDateMillis());
	}
	public ImmutableFXDayEndPacket(FXDailyData dailyData) {
		this(dailyData, new OutbackIdentifier(dailyData.getAddress()));
	}

	@NotNull
	@Override
	public Long getStartDateMillis() {
		return requireNonNull(super.getStartDateMillis());
	}

	@NotNull
	@Override
	public SolarEventPacketType getPacketType() {
		return SolarEventPacketType.FX_DAILY_DAY_END;
	}

	@JsonPOJOBuilder
	static class Builder {
		@JsonUnwrapped
		@JsonProperty(required = true)
		@JsonDeserialize(as = ImmutableFXDailyData.class)
		private FXDailyData fxDailyData;

		public ImmutableFXDayEndPacket build(){
			return new ImmutableFXDayEndPacket(fxDailyData);
		}
	}
}

package me.retrodaredevil.solarthing.solar.outback.mx.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import me.retrodaredevil.solarthing.solar.event.SolarEventPacketType;
import me.retrodaredevil.solarthing.solar.outback.OutbackIdentifier;
import me.retrodaredevil.solarthing.solar.outback.mx.common.BaseMXDailyData;
import me.retrodaredevil.solarthing.solar.outback.mx.common.ImmutableMXDailyData;
import me.retrodaredevil.solarthing.solar.outback.mx.common.MXDailyData;

import me.retrodaredevil.solarthing.annotations.NotNull;

@JsonDeserialize(builder = ImmutableMXDayEndPacket.Builder.class)
public class ImmutableMXDayEndPacket extends BaseMXDailyData implements MXDayEndPacket {
	public ImmutableMXDayEndPacket(MXDailyData dailyData, OutbackIdentifier outbackIdentifier) {
		super(SolarEventPacketType.MXFM_DAILY_DAY_END, dailyData, outbackIdentifier);
	}
	public ImmutableMXDayEndPacket(MXDailyData dailyData) {
		this(dailyData, new OutbackIdentifier(dailyData.getAddress()));
	}

	@NotNull
    @Override
	public SolarEventPacketType getPacketType() {
		return SolarEventPacketType.MXFM_DAILY_DAY_END;
	}

	@JsonPOJOBuilder
	static class Builder {
		@JsonUnwrapped
		@JsonProperty(required = true)
		@JsonDeserialize(as = ImmutableMXDailyData.class)
		private MXDailyData mxDailyData;

		public ImmutableMXDayEndPacket build(){
			return new ImmutableMXDayEndPacket(mxDailyData);
		}
	}
}

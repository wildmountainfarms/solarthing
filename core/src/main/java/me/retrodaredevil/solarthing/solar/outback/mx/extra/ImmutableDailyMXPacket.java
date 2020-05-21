package me.retrodaredevil.solarthing.solar.outback.mx.extra;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.solar.extra.SolarExtraPacketType;
import me.retrodaredevil.solarthing.solar.outback.OutbackIdentifier;
import me.retrodaredevil.solarthing.solar.outback.mx.common.BaseMXDailyData;
import me.retrodaredevil.solarthing.solar.outback.mx.common.ImmutableMXDailyData;
import me.retrodaredevil.solarthing.solar.outback.mx.common.MXDailyData;

@JsonDeserialize(builder = ImmutableDailyMXPacket.Builder.class)
public class ImmutableDailyMXPacket extends BaseMXDailyData implements DailyMXPacket {
	@Deprecated
	public ImmutableDailyMXPacket(MXDailyData dailyData, OutbackIdentifier outbackIdentifier) {
		super(SolarExtraPacketType.MXFM_DAILY, dailyData, outbackIdentifier);
	}
	@Deprecated
	public ImmutableDailyMXPacket(MXDailyData dailyData) {
		this(dailyData, new OutbackIdentifier(dailyData.getAddress()));
	}

	static class Builder {
		@JsonUnwrapped
		@JsonProperty(required = true)
		@JsonDeserialize(as = ImmutableMXDailyData.class)
		private MXDailyData mxDailyData;

		public ImmutableDailyMXPacket build(){
			return new ImmutableDailyMXPacket(mxDailyData);
		}
	}
}

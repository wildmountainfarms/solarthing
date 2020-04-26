package me.retrodaredevil.solarthing.solar.extra;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import org.jetbrains.annotations.Nullable;

import javax.validation.constraints.NotNull;

@JsonDeserialize(as = ImmutableDailyUpdatePacket.class)
@JsonTypeName("DAILY_UPDATE")
@JsonExplicit
public interface DailyUpdatePacket extends SupplementarySolarExtraPacket {
	@NotNull
	@Override
	default SolarExtraPacketType getPacketType() {
		return SolarExtraPacketType.DAILY_UPDATE;
	}

	@JsonProperty("lastResetTimeMillis")
	@Nullable Long getLastResetTimeMillis();

	@JsonProperty("lastAtZeroTimeMillis")
	@Nullable Long getLastAtZeroTimeMillis();

	@JsonProperty("lastIncrementTimeMillis")
	@Nullable Long getLastIncrementTimeMillis();

	@JsonProperty("monitorStartTimeMillis")
	long getMonitorStartTimeMillis();

}

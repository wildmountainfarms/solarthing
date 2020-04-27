package me.retrodaredevil.solarthing.solar.extra;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import org.jetbrains.annotations.Nullable;

import javax.validation.constraints.NotNull;

/**
 * This was a packet that was never put into a database. The point of this was to add more information to packets such as those
 * implementing {@link me.retrodaredevil.solarthing.solar.common.DailyChargeController}. This would allow people who query one of
 * the charge controller packets to instantly know as much information as possible without querying past packets.
 * <p>
 * I, Joshua Shannon, decided not to use this because of all the past data that Wild Mountain Farms SolarThing has.
 * There are better ways to find the same information that this tries to make easier that will also work with past data.
 * Ex: querying the entire day and figuring stuff out.
 */
@JsonDeserialize(as = ImmutableDailyUpdatePacket.class)
@JsonTypeName("DAILY_UPDATE")
@JsonExplicit
@Deprecated
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

//	Long getLastIncrementFromZeroTimeMillis(); // 0 -> 0.1

	@JsonProperty("lastIncrementTimeMillis")
	@Nullable Long getLastIncrementTimeMillis();

	@JsonProperty("monitorStartTimeMillis")
	long getMonitorStartTimeMillis();

}

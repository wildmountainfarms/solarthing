package me.retrodaredevil.solarthing.solar.outback.fx.charge;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.solar.outback.OutbackData;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@JsonDeserialize(as = ImmutableFXChargingPacket.class)
@JsonExplicit
@NullMarked
public interface FXChargingPacket extends OutbackData, Packet {
	@JsonProperty("address")
	@Override
	int getAddress();

	@JsonProperty("fxChargingMode")
	@Nullable FXChargingMode getFXChargingMode();

	@JsonProperty("remainingAbsorbTimeMillis")
	long getRemainingAbsorbTimeMillis();
	@JsonProperty("remainingFloatTimeMillis")
	long getRemainingFloatTimeMillis();
	@JsonProperty("remainingEqualizeTimeMillis")
	long getRemainingEqualizeTimeMillis();


	@JsonProperty("totalAbsorbTimeMillis")
	long getTotalAbsorbTimeMillis();
	@JsonProperty("totalFloatTimeMillis")
	long getTotalFloatTimeMillis();
	@JsonProperty("totalEqualizeTimeMillis")
	long getTotalEqualizeTimeMillis();

}

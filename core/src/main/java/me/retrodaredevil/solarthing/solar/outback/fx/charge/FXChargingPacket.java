package me.retrodaredevil.solarthing.solar.outback.fx.charge;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.solar.extra.SolarExtraPacketType;
import me.retrodaredevil.solarthing.solar.extra.SupplementarySolarExtraPacket;
import org.jetbrains.annotations.Nullable;

import javax.validation.constraints.NotNull;

@JsonDeserialize(as = ImmutableFXChargingPacket.class)
@JsonTypeName("FX_CHARGING")
@JsonExplicit
public interface FXChargingPacket extends SupplementarySolarExtraPacket {
	@NotNull
    @Override
	default SolarExtraPacketType getPacketType(){
		return SolarExtraPacketType.FX_CHARGING;
	}
	@JsonProperty("masterFXAddress")
	int getMasterFXAddress();

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

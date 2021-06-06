package me.retrodaredevil.solarthing.solar.tracer;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;

public class TracerIdentityInfo implements IdentityInfo {
	private final @Nullable Float ratedChargingCurrent;

	public TracerIdentityInfo(@Nullable Float ratedChargingCurrent) {
		this.ratedChargingCurrent = ratedChargingCurrent;
	}

	@Override
	public @NotNull String getName() {
		return "Tracer";
	}

	@Override
	public @NotNull String getSuffix() {
		if (ratedChargingCurrent != null) {
			return Math.round(ratedChargingCurrent) + "A";
		}
		return "";
	}

	@Override
	public @NotNull String getShortName() {
		return "TCR";
	}
}

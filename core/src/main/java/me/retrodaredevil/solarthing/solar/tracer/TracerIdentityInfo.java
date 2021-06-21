package me.retrodaredevil.solarthing.solar.tracer;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;

public class TracerIdentityInfo implements IdentityInfo {
	private final @Nullable Integer ratedChargingCurrent;

	/**
	 * @param ratedChargingCurrent The rated charging/output current
	 */
	public TracerIdentityInfo(@Nullable Integer ratedChargingCurrent) {
		this.ratedChargingCurrent = ratedChargingCurrent;
	}

	@Override
	public @NotNull String getName() {
		return "Tracer";
	}

	@Override
	public @NotNull String getSuffix() {
		if (ratedChargingCurrent != null) {
			return ratedChargingCurrent + "A";
		}
		return "";
	}

	@Override
	public @NotNull String getShortName() {
		return "TCR";
	}
}

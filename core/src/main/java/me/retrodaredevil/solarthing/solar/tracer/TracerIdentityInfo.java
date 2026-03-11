package me.retrodaredevil.solarthing.solar.tracer;

import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class TracerIdentityInfo implements IdentityInfo {
	private final @Nullable Integer ratedChargingCurrent;

	/**
	 * @param ratedChargingCurrent The rated charging/output current
	 */
	public TracerIdentityInfo(@Nullable Integer ratedChargingCurrent) {
		this.ratedChargingCurrent = ratedChargingCurrent;
	}

	@Override
	public @NonNull String getName() {
		return "Tracer";
	}

	@Override
	public @NonNull String getSuffix() {
		if (ratedChargingCurrent != null) {
			return ratedChargingCurrent + "A";
		}
		return "";
	}

	@Override
	public @NonNull String getShortName() {
		return "TCR";
	}

	@Override
	public IdentityInfo stripExtra() {
		return new TracerIdentityInfo(null);
	}
}

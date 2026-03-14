package me.retrodaredevil.solarthing.solar.tracer;

import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class TracerIdentityInfo implements IdentityInfo {
	private final @Nullable Integer ratedChargingCurrent;

	/**
	 * @param ratedChargingCurrent The rated charging/output current
	 */
	public TracerIdentityInfo(@Nullable Integer ratedChargingCurrent) {
		this.ratedChargingCurrent = ratedChargingCurrent;
	}

	@Override
	public String getName() {
		return "Tracer";
	}

	@Override
	public String getSuffix() {
		if (ratedChargingCurrent != null) {
			return ratedChargingCurrent + "A";
		}
		return "";
	}

	@Override
	public String getShortName() {
		return "TCR";
	}

	@Override
	public IdentityInfo stripExtra() {
		return new TracerIdentityInfo(null);
	}
}

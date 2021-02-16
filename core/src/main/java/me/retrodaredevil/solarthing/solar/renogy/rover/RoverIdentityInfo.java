package me.retrodaredevil.solarthing.solar.renogy.rover;

import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;

public class RoverIdentityInfo implements IdentityInfo {
	private final @Nullable Integer ratedChargingCurrent;
	private final @Nullable RoverVariant roverVariant;

	public RoverIdentityInfo(@Nullable Integer ratedChargingCurrent, @Nullable RoverVariant roverVariant) {
		this.ratedChargingCurrent = ratedChargingCurrent;
		this.roverVariant = roverVariant;
	}

	@Override
	public String getName() {
		if (roverVariant == RoverVariant.DCDC) {
			return "DCDC";
		}
		if (roverVariant == RoverVariant.ROVER_BOOST) {
			return "Rover Boost";
		}
		if (roverVariant == RoverVariant.ROVER_ELITE) {
			return "Rover Elite";
		}
		if (roverVariant == RoverVariant.WANDERER) {
			return "Wanderer";
		}
		if (roverVariant == RoverVariant.COMET) {
			return "Comet";
		}
		if (roverVariant == RoverVariant.ZENITH) {
			return "Zenith";
		}
		return "Rover";
	}

	@Override
	public boolean isSuffixMeaningful() {
		return false;
	}

	@Override
	public String getSuffix() {
		return ratedChargingCurrent + "A";
	}

	@Override
	public String getShortName() {

		if (roverVariant == RoverVariant.DCDC) {
			return "DCC";
		}
		if (roverVariant == RoverVariant.ROVER_BOOST) {
			return "RVB";
		}
		if (roverVariant == RoverVariant.ROVER_ELITE) {
			return "RVE";
		}
		if (roverVariant == RoverVariant.WANDERER) {
			return "WND";
		}
		if (roverVariant == RoverVariant.COMET) {
			return "CMT";
		}
		if (roverVariant == RoverVariant.ZENITH) {
			return "ZNT";
		}
		return "RV";
	}

	@Override
	public IdentityInfo stripExtra() {
		if (ratedChargingCurrent == null && roverVariant == null) {
			return this;
		}
		return new RoverIdentityInfo(null, null);
	}
}

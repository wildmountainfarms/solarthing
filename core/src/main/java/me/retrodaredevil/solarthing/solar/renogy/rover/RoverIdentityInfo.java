package me.retrodaredevil.solarthing.solar.renogy.rover;

import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;

public class RoverIdentityInfo implements IdentityInfo {
	private final long productSerialNumber;

	public RoverIdentityInfo(long productSerialNumber) {
		this.productSerialNumber = productSerialNumber;
	}

	@Override
	public String getDisplayName() {
		return "Rover " + productSerialNumber;
	}
}

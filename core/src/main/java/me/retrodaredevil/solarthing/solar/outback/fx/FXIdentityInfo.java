package me.retrodaredevil.solarthing.solar.outback.fx;

import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;

public class FXIdentityInfo implements IdentityInfo {
	private final int address;

	public FXIdentityInfo(int address) {
		this.address = address;
	}

	@Override
	public String getDisplayName() {
		return "FX " + address;
	}
}

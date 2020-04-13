package me.retrodaredevil.solarthing.solar.outback.mx;

import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;

public class MXIdentityInfo implements IdentityInfo {
	private final int address;

	public MXIdentityInfo(int address) {
		this.address = address;
	}

	@Override
	public String getDisplayName() {
		return "MX " + address;
	}
}

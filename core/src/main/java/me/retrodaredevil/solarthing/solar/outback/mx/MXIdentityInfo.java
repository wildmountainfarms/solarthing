package me.retrodaredevil.solarthing.solar.outback.mx;

import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;

public class MXIdentityInfo implements IdentityInfo {
	private final int address;

	public MXIdentityInfo(int address) {
		this.address = address;
	}

	@Override
	public String getName() {
		return "MX";
	}

	@Override
	public String getShortName() {
		return "MX";
	}

	@Override
	public String getSuffix() {
		return "" + address;
	}
}

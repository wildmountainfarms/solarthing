package me.retrodaredevil.solarthing.solar.pzem;

import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;

public class PzemShuntIdentityInfo implements IdentityInfo {
	private final int dataId;

	public PzemShuntIdentityInfo(int dataId) {
		this.dataId = dataId;
	}

	@Override
	public String getName() {
		return "Pzem Shunt";
	}

	@Override
	public String getSuffix() {
		return "" + dataId;
	}

	@Override
	public String getShortName() {
		return "PZ";
	}
}

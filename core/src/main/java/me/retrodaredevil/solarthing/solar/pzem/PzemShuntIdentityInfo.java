package me.retrodaredevil.solarthing.solar.pzem;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;

public class PzemShuntIdentityInfo implements IdentityInfo {
	private final int dataId;

	public PzemShuntIdentityInfo(int dataId) {
		this.dataId = dataId;
	}

	@Override
	public @NotNull String getName() {
		return "Pzem Shunt";
	}

	@Override
	public @NotNull String getSuffix() {
		return "" + dataId;
	}

	@Override
	public @NotNull String getShortName() {
		return "PZ";
	}
}

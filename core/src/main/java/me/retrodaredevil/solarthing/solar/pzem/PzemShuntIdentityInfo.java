package me.retrodaredevil.solarthing.solar.pzem;

import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;
import org.jspecify.annotations.NonNull;

public class PzemShuntIdentityInfo implements IdentityInfo {
	private final int dataId;

	public PzemShuntIdentityInfo(int dataId) {
		this.dataId = dataId;
	}

	@Override
	public @NonNull String getName() {
		return "Pzem Shunt";
	}

	@Override
	public @NonNull String getSuffix() {
		return "" + dataId;
	}

	@Override
	public @NonNull String getShortName() {
		return "PZ";
	}
}

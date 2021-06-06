package me.retrodaredevil.solarthing.solar.outback.fx;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;

public class FXIdentityInfo implements IdentityInfo {
	private final int address;

	public FXIdentityInfo(int address) {
		this.address = address;
	}

	@Override
	public @NotNull String getName() {
		return "FX";
	}

	@Override
	public @NotNull String getShortName() {
		return "FX";
	}

	@Override
	public @NotNull String getSuffix() {
		return "" + address;
	}
}

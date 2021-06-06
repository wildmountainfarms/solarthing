package me.retrodaredevil.solarthing.solar.outback.mx;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;

public class MXIdentityInfo implements IdentityInfo {
	private final int address;

	public MXIdentityInfo(int address) {
		this.address = address;
	}

	@Override
	public @NotNull String getName() {
		return "MX";
	}

	@Override
	public @NotNull String getShortName() {
		return "MX";
	}

	@Override
	public @NotNull String getSuffix() {
		return "" + address;
	}
}

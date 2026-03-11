package me.retrodaredevil.solarthing.solar.outback.fx;

import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;
import org.jspecify.annotations.NonNull;

public class FXIdentityInfo implements IdentityInfo {
	private final int address;

	public FXIdentityInfo(int address) {
		this.address = address;
	}

	@Override
	public @NonNull String getName() {
		return "FX";
	}

	@Override
	public @NonNull String getShortName() {
		return "FX";
	}

	@Override
	public @NonNull String getSuffix() {
		return "" + address;
	}
}

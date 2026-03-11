package me.retrodaredevil.solarthing.solar.outback.mx;

import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;
import org.jspecify.annotations.NonNull;

public class MXIdentityInfo implements IdentityInfo {
	private final int address;

	public MXIdentityInfo(int address) {
		this.address = address;
	}

	@Override
	public @NonNull String getName() {
		return "MX";
	}

	@Override
	public @NonNull String getShortName() {
		return "MX";
	}

	@Override
	public @NonNull String getSuffix() {
		return "" + address;
	}
}

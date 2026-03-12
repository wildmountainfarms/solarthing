package me.retrodaredevil.solarthing.solar.outback.mx;

import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class MXIdentityInfo implements IdentityInfo {
	private final int address;

	public MXIdentityInfo(int address) {
		this.address = address;
	}

	// TODO remove NonNull
	@Override
	public @NonNull String getName() {
		return "MX";
	}

	// TODO remove NonNull
	@Override
	public @NonNull String getShortName() {
		return "MX";
	}

	// TODO remove NonNull
	@Override
	public @NonNull String getSuffix() {
		return "" + address;
	}
}

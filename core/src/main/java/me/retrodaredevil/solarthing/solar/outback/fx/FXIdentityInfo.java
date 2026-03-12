package me.retrodaredevil.solarthing.solar.outback.fx;

import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class FXIdentityInfo implements IdentityInfo {
	private final int address;

	public FXIdentityInfo(int address) {
		this.address = address;
	}

	// TODO remove NonNull
	@Override
	public @NonNull String getName() {
		return "FX";
	}

	// TODO remove NonNull
	@Override
	public @NonNull String getShortName() {
		return "FX";
	}

	// TODO remove NonNull
	@Override
	public @NonNull String getSuffix() {
		return "" + address;
	}
}

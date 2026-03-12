package me.retrodaredevil.solarthing.misc.device;

import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class DeviceIdentityInfo implements IdentityInfo {
	// TODO remove NonNull
	@Override
	public @NonNull String getName() {
		return "Device";
	}

	@Override
	public @NonNull String getSuffix() {
		return "";
	}

	@Override
	public boolean isSuffixMeaningful() {
		return false;
	}

	@Override
	public @NonNull String getShortName() {
		return "DEV";
	}
}

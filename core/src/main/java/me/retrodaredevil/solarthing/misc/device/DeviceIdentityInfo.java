package me.retrodaredevil.solarthing.misc.device;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;

public class DeviceIdentityInfo implements IdentityInfo {
	@Override
	public @NotNull String getName() {
		return "Device";
	}

	@Override
	public @NotNull String getSuffix() {
		return "";
	}

	@Override
	public boolean isSuffixMeaningful() {
		return false;
	}

	@Override
	public @NotNull String getShortName() {
		return "DEV";
	}
}

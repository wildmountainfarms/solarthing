package me.retrodaredevil.solarthing.misc.device;

import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;

public class DeviceIdentityInfo implements IdentityInfo {
	@Override
	public String getName() {
		return "Device";
	}

	@Override
	public String getSuffix() {
		return "";
	}

	@Override
	public boolean isSuffixMeaningful() {
		return false;
	}

	@Override
	public String getShortName() {
		return "DEV";
	}
}

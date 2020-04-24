package me.retrodaredevil.solarthing.misc.device;

import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;

public class DeviceIdentityInfo implements IdentityInfo {
	@Override
	public String getDisplayName() {
		return "Device";
	}
}

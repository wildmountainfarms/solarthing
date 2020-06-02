package me.retrodaredevil.solarthing.misc.weather;

import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;

public class TemperatureIdentityInfo implements IdentityInfo {
	private final int dataId;

	public TemperatureIdentityInfo(int dataId) {
		this.dataId = dataId;
	}

	@Override
	public String getDisplayName() {
		return "Temperature Sensor " + dataId;
	}
}

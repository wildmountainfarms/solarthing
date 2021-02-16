package me.retrodaredevil.solarthing.misc.weather;

import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;

public class TemperatureIdentityInfo implements IdentityInfo {
	private final int dataId;

	public TemperatureIdentityInfo(int dataId) {
		this.dataId = dataId;
	}

	@Override
	public String getName() {
		return "Temperature Sensor";
	}

	@Override
	public String getShortName() {
		return "TMP";
	}

	@Override
	public String getSuffix() {
		return "" + dataId;
	}
}

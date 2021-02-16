package me.retrodaredevil.solarthing.solar.batteryvoltage;

import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;

public class BatteryVoltageOnlyIdentityInfo implements IdentityInfo {
	private final int dataId;

	public BatteryVoltageOnlyIdentityInfo(int dataId) {
		this.dataId = dataId;
	}

	@Override
	public String getName() {
		return "Battery Voltage";
	}

	@Override
	public String getSuffix() {
		return "" + dataId;
	}

	@Override
	public String getShortName() {
		return "BAT";
	}
}

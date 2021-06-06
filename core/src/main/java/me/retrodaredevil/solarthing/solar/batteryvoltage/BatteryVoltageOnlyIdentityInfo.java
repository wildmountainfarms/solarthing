package me.retrodaredevil.solarthing.solar.batteryvoltage;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;

public class BatteryVoltageOnlyIdentityInfo implements IdentityInfo {
	private final int dataId;

	public BatteryVoltageOnlyIdentityInfo(int dataId) {
		this.dataId = dataId;
	}

	@Override
	public @NotNull String getName() {
		return "Battery Voltage";
	}

	@Override
	public @NotNull String getSuffix() {
		return "" + dataId;
	}

	@Override
	public @NotNull String getShortName() {
		return "BAT";
	}
}

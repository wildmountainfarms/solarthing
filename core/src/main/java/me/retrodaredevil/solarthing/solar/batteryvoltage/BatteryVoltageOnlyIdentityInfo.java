package me.retrodaredevil.solarthing.solar.batteryvoltage;

import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;
import org.jspecify.annotations.NonNull;

public class BatteryVoltageOnlyIdentityInfo implements IdentityInfo {
	private final int dataId;

	public BatteryVoltageOnlyIdentityInfo(int dataId) {
		this.dataId = dataId;
	}

	@Override
	public @NonNull String getName() {
		return "Battery Voltage";
	}

	@Override
	public @NonNull String getSuffix() {
		return "" + dataId;
	}

	@Override
	public @NonNull String getShortName() {
		return "BAT";
	}
}

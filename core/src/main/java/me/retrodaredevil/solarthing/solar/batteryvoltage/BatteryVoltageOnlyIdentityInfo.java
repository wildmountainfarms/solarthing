package me.retrodaredevil.solarthing.solar.batteryvoltage;

import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class BatteryVoltageOnlyIdentityInfo implements IdentityInfo {
	private final int dataId;

	public BatteryVoltageOnlyIdentityInfo(int dataId) {
		this.dataId = dataId;
	}

	// TODO remove NonNull
	@Override
	public @NonNull String getName() {
		return "Battery Voltage";
	}

	// TODO remove NonNull
	@Override
	public @NonNull String getSuffix() {
		return "" + dataId;
	}

	// TODO remove NonNull
	@Override
	public @NonNull String getShortName() {
		return "BAT";
	}
}

package me.retrodaredevil.solarthing.misc.weather;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;

public class TemperatureIdentityInfo implements IdentityInfo {
	private final int dataId;

	public TemperatureIdentityInfo(int dataId) {
		this.dataId = dataId;
	}

	@Override
	public @NotNull String getName() {
		return "Temperature Sensor";
	}

	@Override
	public @NotNull String getShortName() {
		return "TMP";
	}

	@Override
	public @NotNull String getSuffix() {
		return "" + dataId;
	}
}

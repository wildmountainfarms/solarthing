package me.retrodaredevil.solarthing.misc.weather;

import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;
import org.jspecify.annotations.NonNull;

public class TemperatureIdentityInfo implements IdentityInfo {
	private final int dataId;

	public TemperatureIdentityInfo(int dataId) {
		this.dataId = dataId;
	}

	@Override
	public @NonNull String getName() {
		return "Temperature Sensor";
	}

	@Override
	public @NonNull String getShortName() {
		return "TMP";
	}

	@Override
	public @NonNull String getSuffix() {
		return "" + dataId;
	}
}

package me.retrodaredevil.solarthing.misc.weather;

import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class TemperatureIdentityInfo implements IdentityInfo {
	private final int dataId;

	public TemperatureIdentityInfo(int dataId) {
		this.dataId = dataId;
	}

	// TODO remove NonNull
	@Override
	public @NonNull String getName() {
		return "Temperature Sensor";
	}

	// TODO remove NonNull
	@Override
	public @NonNull String getShortName() {
		return "TMP";
	}

	// TODO remove NonNull
	@Override
	public @NonNull String getSuffix() {
		return "" + dataId;
	}
}

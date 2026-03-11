package me.retrodaredevil.solarthing.misc.common.meta;

import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;
import org.jspecify.annotations.NonNull;

public class DataMetaIdentityInfo implements IdentityInfo {
	private final String name;
	private final int dataId;

	public DataMetaIdentityInfo(String name, int dataId) {
		this.name = name;
		this.dataId = dataId;
	}

	@Override
	public @NonNull String getName() {
		return name;
	}

	@Override
	public @NonNull String getSuffix() {
		return "" + dataId;
	}

	@Override
	public @NonNull String getShortName() {
		return name;
	}
}

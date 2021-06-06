package me.retrodaredevil.solarthing.misc.common.meta;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;

public class DataMetaIdentityInfo implements IdentityInfo {
	private final String name;
	private final int dataId;

	public DataMetaIdentityInfo(String name, int dataId) {
		this.name = name;
		this.dataId = dataId;
	}

	@Override
	public @NotNull String getName() {
		return name;
	}

	@Override
	public @NotNull String getSuffix() {
		return "" + dataId;
	}

	@Override
	public @NotNull String getShortName() {
		return name;
	}
}

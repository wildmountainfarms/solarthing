package me.retrodaredevil.solarthing.misc.common.meta;

import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;

public class DataMetaIdentityInfo implements IdentityInfo {
	private final String name;
	private final int dataId;

	public DataMetaIdentityInfo(String name, int dataId) {
		this.name = name;
		this.dataId = dataId;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getSuffix() {
		return "" + dataId;
	}

	@Override
	public String getShortName() {
		return name;
	}
}

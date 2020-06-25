package me.retrodaredevil.solarthing.misc.common.meta;

import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;

public class DataMetaIdentityInfo implements IdentityInfo {
	private final String name;

	public DataMetaIdentityInfo(String name) {
		this.name = name;
	}

	@Override
	public String getDisplayName() {
		return name;
	}
}

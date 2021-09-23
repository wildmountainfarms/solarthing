package me.retrodaredevil.solarthing.type.closed.meta;

import java.util.Collections;
import java.util.List;

public class EmptyMetaDatabase implements MetaDatabase {

	private static final MetaDatabase INSTANCE = new EmptyMetaDatabase();

	public static MetaDatabase getInstance() {
		return INSTANCE;
	}

	@Override
	public List<TargetedMetaPacket> getMeta(long dateMillis, int fragmentId) {
		return Collections.emptyList();
	}

	@Override
	public List<BasicMetaPacket> getMeta(long dateMillis) {
		return Collections.emptyList();
	}
}

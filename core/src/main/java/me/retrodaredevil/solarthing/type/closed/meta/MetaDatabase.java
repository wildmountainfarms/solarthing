package me.retrodaredevil.solarthing.type.closed.meta;

import java.util.List;

public interface MetaDatabase {
	List<TargetedMetaPacket> getMeta(long dateMillis, int fragmentId);
	List<BasicMetaPacket> getMeta(long dateMillis);
}

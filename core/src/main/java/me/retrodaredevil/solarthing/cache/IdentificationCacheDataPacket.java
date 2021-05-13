package me.retrodaredevil.solarthing.cache;

import java.util.List;

public interface IdentificationCacheDataPacket<T extends IdentificationCacheNode> extends CacheDataPacket {
	List<T> getNodes();
}

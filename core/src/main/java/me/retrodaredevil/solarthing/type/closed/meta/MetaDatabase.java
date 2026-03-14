package me.retrodaredevil.solarthing.type.closed.meta;

import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public interface MetaDatabase {
	List<TargetedMetaPacket> getMeta(long dateMillis, int fragmentId);
	List<BasicMetaPacket> getMeta(long dateMillis);
}

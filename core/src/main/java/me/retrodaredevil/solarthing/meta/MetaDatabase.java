package me.retrodaredevil.solarthing.meta;

import me.retrodaredevil.solarthing.packets.Packet;

import java.util.List;

public interface MetaDatabase {
	List<Packet> getMeta(int fragmentId);
	List<Packet> getMetaFromInstant(long dateMillis, int fragmentId);
	List<Packet> getMetaFromInstant(long dateMillis);
}

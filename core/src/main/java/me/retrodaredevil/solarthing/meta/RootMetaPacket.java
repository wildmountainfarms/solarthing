package me.retrodaredevil.solarthing.meta;

import me.retrodaredevil.solarthing.packets.PacketEntry;

import java.util.List;
import java.util.Map;

public interface RootMetaPacket extends PacketEntry {
	@Override
	default String getDbId() {
		return "meta";
	}

//	Map<Integer, List<TimedMetaCollection>> getData();
	List<TimedMetaCollection> getData();
}

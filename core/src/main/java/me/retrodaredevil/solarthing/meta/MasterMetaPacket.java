package me.retrodaredevil.solarthing.meta;

import java.util.List;
import java.util.Map;

public interface MasterMetaPacket {
	Map<String, Map<Integer, List<TimedMetaCollection>>> getData();
}

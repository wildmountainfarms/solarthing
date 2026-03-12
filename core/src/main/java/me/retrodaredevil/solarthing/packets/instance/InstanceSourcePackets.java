package me.retrodaredevil.solarthing.packets.instance;

import me.retrodaredevil.solarthing.annotations.UtilityClass;
import org.jspecify.annotations.NullMarked;

@UtilityClass
@NullMarked
public final class InstanceSourcePackets {
	private InstanceSourcePackets(){ throw new UnsupportedOperationException(); }
	public static InstanceSourcePacket create(String sourceId){
		return new ImmutableInstanceSourcePacket(sourceId);
	}
}

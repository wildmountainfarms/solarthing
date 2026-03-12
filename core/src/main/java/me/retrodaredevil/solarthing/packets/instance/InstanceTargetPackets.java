package me.retrodaredevil.solarthing.packets.instance;

import me.retrodaredevil.solarthing.annotations.UtilityClass;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;

@UtilityClass
@NullMarked
public final class InstanceTargetPackets {
	private InstanceTargetPackets(){ throw new UnsupportedOperationException(); }

	public static InstanceTargetPacket create(Collection<Integer> targetFragmentIds){
		return new ImmutableInstanceTargetPacket(targetFragmentIds);
	}
}

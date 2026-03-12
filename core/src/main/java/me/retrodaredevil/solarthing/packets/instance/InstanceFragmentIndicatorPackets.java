package me.retrodaredevil.solarthing.packets.instance;

import me.retrodaredevil.solarthing.annotations.UtilityClass;
import org.jspecify.annotations.NullMarked;

@UtilityClass
@NullMarked
public final class InstanceFragmentIndicatorPackets {
	private InstanceFragmentIndicatorPackets(){ throw new UnsupportedOperationException(); }

	public static InstanceFragmentIndicatorPacket create(int fragmentId){
		return new ImmutableInstanceFragmentIndicatorPacket(fragmentId);
	}
}

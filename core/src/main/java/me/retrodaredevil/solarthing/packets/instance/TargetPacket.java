package me.retrodaredevil.solarthing.packets.instance;

import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.packets.Packet;

import java.util.Collection;

public interface TargetPacket extends Packet {
	@Nullable Collection<Integer> getTargetFragmentIds();

	default boolean isTargetingAll() {
		return getTargetFragmentIds() == null;
	}
	default boolean isTargetingMultiple() {
		Collection<Integer> targets = getTargetFragmentIds();
		return targets == null || targets.size() > 1;
	}
	default boolean isTarget(int fragmentId) {
		Collection<Integer> targets = getTargetFragmentIds();
		return targets == null || targets.contains(fragmentId);
	}
}

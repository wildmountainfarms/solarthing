package me.retrodaredevil.solarthing;

import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface FragmentedPacketGroupProvider extends PacketGroupProvider {
	@Override
	@Nullable FragmentedPacketGroup getPacketGroup();
}

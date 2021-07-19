package me.retrodaredevil.solarthing;

import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;

public interface FragmentedPacketGroupProvider extends PacketGroupProvider {
	@Override
	FragmentedPacketGroup getPacketGroup();
}

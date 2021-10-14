package me.retrodaredevil.action.node.environment;

import me.retrodaredevil.solarthing.FragmentedPacketGroupProvider;

/**
 * Contains a {@link FragmentedPacketGroupProvider} that returns the latest {@link me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup}.
 * <p>
 * Note that unless necessary, you should use {@link LatestPacketGroupEnvironment} if you can use a {@link me.retrodaredevil.solarthing.packets.collection.PacketGroup} instead
 * of a {@link me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup}.
 * <p>
 * This environment may not be available for all actions, as the use of a {@link me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup} usually
 * implies the querying of a database and the combining of packets from multiple fragments.
 */
public class LatestFragmentedPacketGroupEnvironment {
	private final FragmentedPacketGroupProvider packetGroupProvider;

	public LatestFragmentedPacketGroupEnvironment(FragmentedPacketGroupProvider packetGroupProvider) {
		this.packetGroupProvider = packetGroupProvider;
	}

	/**
	 * Note: In the automation program, the returned {@link FragmentedPacketGroupProvider} may return null.
	 * @return The {@link FragmentedPacketGroupProvider}.
	 */
	public FragmentedPacketGroupProvider getFragmentedPacketGroupProvider() {
		return packetGroupProvider;
	}
}

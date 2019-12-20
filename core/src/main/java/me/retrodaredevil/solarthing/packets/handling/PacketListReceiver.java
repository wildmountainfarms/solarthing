package me.retrodaredevil.solarthing.packets.handling;

import me.retrodaredevil.solarthing.packets.Packet;

import java.util.List;

@FunctionalInterface
public interface PacketListReceiver {
	/**
	 * When calling this method, you can expect that {@code packets} may be mutated.
	 * <p>
	 * It is also expected that after this method is called, the caller may mutate or clear {@code packets}, so if the implementation
	 * needs a reference to {@code packets}, it should copy the elements into a new list.
	 * @param packets
	 * @param wasInstant
	 */
	void receive(List<Packet> packets, boolean wasInstant);
}

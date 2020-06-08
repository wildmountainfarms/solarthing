package me.retrodaredevil.solarthing.packets.handling;

import me.retrodaredevil.solarthing.InstantType;
import me.retrodaredevil.solarthing.packets.Packet;

import java.util.List;

/**
 * Represents something that accepts a list of packets. This is a broad interface, so its behaviour is NOT strictly defined.
 */
@FunctionalInterface
public interface PacketListReceiver {
	/**
	 * When calling this method, you can expect that {@code packets} may be mutated.
	 * <p>
	 * It is also expected that after this method is called, the caller may mutate or clear {@code packets}, so if the implementation
	 * needs a reference to {@code packets}, it should copy the elements into a new list.
	 * @param packets The list of packets. The implementation may add or remove packets depending on what it's supposed to do.
	 */
	void receive(List<Packet> packets, InstantType instantType);
}

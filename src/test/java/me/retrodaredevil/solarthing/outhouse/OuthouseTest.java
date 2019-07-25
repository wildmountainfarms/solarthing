package me.retrodaredevil.solarthing.outhouse;

import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.creation.PacketCreationException;
import me.retrodaredevil.solarthing.packets.creation.PacketCreator;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class OuthouseTest {
	@Test
	void testDoor() throws PacketCreationException {
		PacketCreator creator = new OuthousePacketCreator();
		Collection<? extends Packet> collection = creator.add("\nDOOR true 10 12\r".toCharArray());
		assertEquals(1, collection.size());
		DoorPacket door = (DoorPacket) collection.iterator().next();
		assertTrue(door.isOpen());
		assertEquals(10, door.getLastCloseTimeMillis());
		assertEquals(12, door.getLastOpenTimeMillis());
	}
}

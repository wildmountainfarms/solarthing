package me.retrodaredevil.solarthing.packets.collection;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PacketGroupsTest {

	@Test
	void testGetClosest() {
		List<InstancePacketGroup> packetGroups = Arrays.asList(create(0), create(5), create(7), create(20), create(30), create(40));
		assertEquals(0, PacketGroups.getClosest(packetGroups, 2).getDateMillis());
		assertEquals(7, PacketGroups.getClosest(packetGroups, 10).getDateMillis());
		assertEquals(40, PacketGroups.getClosest(packetGroups, 36).getDateMillis());
		assertEquals(40, PacketGroups.getClosest(packetGroups, 50).getDateMillis());
	}
	@Test
	void testGetClosest2() {
		List<InstancePacketGroup> packetGroups = Arrays.asList(create(0), create(5), create(7), create(20), create(30), create(40), create(100));
		assertEquals(0, PacketGroups.getClosest(packetGroups, 2).getDateMillis());
		assertEquals(7, PacketGroups.getClosest(packetGroups, 10).getDateMillis());
		assertEquals(40, PacketGroups.getClosest(packetGroups, 36).getDateMillis());
		assertEquals(40, PacketGroups.getClosest(packetGroups, 50).getDateMillis());
		assertEquals(100, PacketGroups.getClosest(packetGroups, 76).getDateMillis());
		assertEquals(100, PacketGroups.getClosest(packetGroups, 100000).getDateMillis());
	}
	private InstancePacketGroup create(long dateMillis) {
		return PacketGroups.createInstancePacketGroup(Collections.emptyList(), dateMillis, "defaultl", 0);
	}
}

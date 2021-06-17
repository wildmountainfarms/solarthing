package me.retrodaredevil.solarthing.packets.collection;

import me.retrodaredevil.solarthing.packets.Packet;
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
		return create(dateMillis, 0);
	}
	private InstancePacketGroup create(long dateMillis, int fragmentId) {
		return PacketGroups.createInstancePacketGroup(Collections.singleton(new Packet() {}), dateMillis, "default", fragmentId);
	}


	@Test
	void testMergePackets_simple() {
		List<InstancePacketGroup> packetGroups = Arrays.asList(
				create(0L, 1),
				create(4L, 3),
				create(5L, 2),
				create(4L, 4)
		);
		List<FragmentedPacketGroup> fragmentedPacketGroups = PacketGroups.mergePackets(packetGroups, 10L, 10L);
		assertEquals(1, fragmentedPacketGroups.size());
		FragmentedPacketGroup fragmentedPacketGroup = fragmentedPacketGroups.get(0);
		assertEquals(4, fragmentedPacketGroup.getPackets().size());
		assertEquals(0L, fragmentedPacketGroup.getDateMillis());

		assertEquals(0L, fragmentedPacketGroup.getDateMillis(fragmentedPacketGroup.getPackets().get(0)));
		assertEquals(1, fragmentedPacketGroup.getFragmentId(fragmentedPacketGroup.getPackets().get(0)));

		// notice that even though the InstancePacketGroup with a fragment ID of 3 was passed first, the one with a packet ID of 2 gets added first
		assertEquals(5L, fragmentedPacketGroup.getDateMillis(fragmentedPacketGroup.getPackets().get(1)));
		assertEquals(2, fragmentedPacketGroup.getFragmentId(fragmentedPacketGroup.getPackets().get(1)));

		assertEquals(4L, fragmentedPacketGroup.getDateMillis(fragmentedPacketGroup.getPackets().get(2)));
		assertEquals(3, fragmentedPacketGroup.getFragmentId(fragmentedPacketGroup.getPackets().get(2)));

		assertEquals(4L, fragmentedPacketGroup.getDateMillis(fragmentedPacketGroup.getPackets().get(3)));
		assertEquals(4, fragmentedPacketGroup.getFragmentId(fragmentedPacketGroup.getPackets().get(3)));
	}

	@Test
	void testMergePackets_ignore_outside_of_range() {
		List<InstancePacketGroup> packetGroups = Arrays.asList(
				create(0L, 1),
				create(5L, 2),
				create(4L, 3),
				create(12L, 4)
		);
		// notice that the master ID ignore distance is 15L, so the 4th fragment should be ignored
		List<FragmentedPacketGroup> fragmentedPacketGroups = PacketGroups.mergePackets(packetGroups, 10L, 15L);
		assertEquals(1, fragmentedPacketGroups.size());
		FragmentedPacketGroup fragmentedPacketGroup = fragmentedPacketGroups.get(0);
		assertEquals(3, fragmentedPacketGroup.getPackets().size());
		assertEquals(0L, fragmentedPacketGroup.getDateMillis());
	}

	@Test
	void testMergePackets_switch_to_new_master() {
		List<InstancePacketGroup> packetGroups = Arrays.asList(
				create(0L, 1),
				create(12L, 4)
		);
		// notice that the master ID ignore distance is 15L, so the 4th fragment should be ignored
		List<FragmentedPacketGroup> fragmentedPacketGroups = PacketGroups.mergePackets(packetGroups, 10L, 10L);
		assertEquals(2, fragmentedPacketGroups.size());
		FragmentedPacketGroup fragmentedPacketGroup1 = fragmentedPacketGroups.get(0);
		assertEquals(1, fragmentedPacketGroup1.getPackets().size());
		assertEquals(0L, fragmentedPacketGroup1.getDateMillis());

		FragmentedPacketGroup fragmentedPacketGroup2 = fragmentedPacketGroups.get(1);
		assertEquals(1, fragmentedPacketGroup2.getPackets().size());
		assertEquals(12L, fragmentedPacketGroup2.getDateMillis());
	}

	@Test
	void testMergePackets_not_switch_to_new_master_when_higher_priority_fragments_extend_range() {
		/*
		Since we have a fragment ID of 2 with a dateMillis of 5L, when 2L becomes the master fragment
		on the ends, masterIdIgnoreDistance should be applied to that, so 5L + 10L = 15L, which means that fragment ID 4 should NOT be included
		anywhere.

		This may seem unintuitive, and honestly, could be changed if we wanted to. For now, we have a test that makes sure this behavior isn't
		accidentally changed. Purposely changing this in the future may not be a bad thing, as long as it is thought through
		 */
		List<InstancePacketGroup> packetGroups = Arrays.asList(
				create(0L, 1),
				create(5L, 2),
				create(4L, 3),
				create(12L, 4)
		);
		// notice that the master ID ignore distance is 15L, so the 4th fragment should be ignored
		List<FragmentedPacketGroup> fragmentedPacketGroups = PacketGroups.mergePackets(packetGroups, 10L, 10L);
		assertEquals(1, fragmentedPacketGroups.size());
		FragmentedPacketGroup fragmentedPacketGroup1 = fragmentedPacketGroups.get(0);
		assertEquals(3, fragmentedPacketGroup1.getPackets().size());
		assertEquals(0L, fragmentedPacketGroup1.getDateMillis());
	}
}

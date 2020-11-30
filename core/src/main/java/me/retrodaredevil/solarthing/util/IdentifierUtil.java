package me.retrodaredevil.solarthing.util;

import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;
import me.retrodaredevil.solarthing.packets.identification.Identifier;

import java.util.List;
import java.util.Map;

public final class IdentifierUtil {
	private IdentifierUtil() { throw new UnsupportedOperationException(); }

	public static boolean meetsRequirements(Map<Integer, List<String>> requiredIdentifierMap, FragmentedPacketGroup latestPacketGroup) {
		return getRequirementNotMetReason(requiredIdentifierMap, latestPacketGroup) == null;
	}

	public static @Nullable String getRequirementNotMetReason(Map<Integer, List<String>> requiredIdentifierMap, FragmentedPacketGroup latestPacketGroup) {
		outerLoop: for (Map.Entry<Integer, List<String>> entry : requiredIdentifierMap.entrySet()) {
			int desiredFragmentId = entry.getKey();
			if (!latestPacketGroup.hasFragmentId(desiredFragmentId)) {
				return "The latest packet group doesn't contain the " + desiredFragmentId + " fragment id!";
			}
			for (String desiredIdentifierRepresentation : entry.getValue()) {
				for (Packet packet : latestPacketGroup.getPackets()) {
					int fragmentId = latestPacketGroup.getFragmentId(packet);
					if (fragmentId != desiredFragmentId) {
						continue;
					}
					if (packet instanceof Identifiable) {
						Identifier identifier = ((Identifiable) packet).getIdentifier();
						if (desiredIdentifierRepresentation.equals(identifier.getRepresentation())) {
							continue outerLoop;
						}
					}
				}
				return "The required identifier: " + entry.getValue() + " with fragmentId: " + entry.getKey() + " was not present in the latest packet group!";
			}
		}
		return null;
	}
}

package me.retrodaredevil.solarthing.solar.accumulation;

import me.retrodaredevil.solarthing.annotations.UtilityClass;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.TimestampedPacket;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;
import me.retrodaredevil.solarthing.packets.identification.IdentifierFragment;
import me.retrodaredevil.solarthing.solar.common.DailyData;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public final class AccumulationUtil {
	private AccumulationUtil(){ throw new UnsupportedOperationException(); }

	/**
	 *
	 * @param isFirst Should be true when the first and end packets are part of the first chunk of data from a certain day.
	 *                Helps determine if the first packet needs to be interpreted as data from today or from yesterday.
	 */
	@Contract(pure = true)
	private static <T extends DailyData> AccumulationPair<T> createAccumulationPair(boolean isFirst, TimestampedPacket<T> firstPacket, TimestampedPacket<T> endPacket, AccumulationConfig accumulationConfig) {
		final AccumulationPair.StartPacketType startPacketType;
		if (isFirst) {
			if (firstPacket.getDateMillis() < accumulationConfig.getCutOffIfStartBeforeDateMillis() || endPacket.getDateMillis() < accumulationConfig.getCutOffIfEndBeforeDateMillis()) {
				startPacketType = AccumulationPair.StartPacketType.CUT_OFF;
			} else {
				startPacketType = AccumulationPair.StartPacketType.MIDDLE_OF_DAY_CONNECT;
			}
		} else {
			startPacketType = AccumulationPair.StartPacketType.FIRST_AFTER_RESET;
		}
		return new AccumulationPair<>(firstPacket, endPacket, startPacketType);
	}
	@Contract(pure = true)
	public static <T extends DailyData> List<AccumulationPair<T>> getAccumulationPairs(List<? extends TimestampedPacket<T>> packets, AccumulationConfig accumulationConfig) {
		List<AccumulationPair<T>> r = new ArrayList<>();
		TimestampedPacket<T> firstPacket = null;
		TimestampedPacket<T> lastPacket = null;
		for (TimestampedPacket<T> packet : packets) {
			if (firstPacket == null) {
				firstPacket = packet;
			}
			if (lastPacket != null) {
				if (packet.getPacket().isNewDay(lastPacket.getPacket())) {
					r.add(createAccumulationPair(r.isEmpty(), firstPacket, lastPacket, accumulationConfig));
					firstPacket = packet;
				}
			}
			lastPacket = packet;
		}
		if (firstPacket != null) {
			//noinspection ConstantConditions
			assert lastPacket != null;
			r.add(createAccumulationPair(r.isEmpty(), firstPacket, lastPacket, accumulationConfig));
		}
		return r;
	}
	@Contract(pure = true)
	public static <T extends DailyData> Map<IdentifierFragment, List<AccumulationPair<T>>> getAccumulationPairs(Map<IdentifierFragment, List<TimestampedPacket<T>>> packetMap, AccumulationConfig accumulationConfig) {
		Map<IdentifierFragment, List<AccumulationPair<T>>> r = new HashMap<>(packetMap.size());
		for (Map.Entry<IdentifierFragment, List<TimestampedPacket<T>>> entry : packetMap.entrySet()) {
			r.put(entry.getKey(), getAccumulationPairs(entry.getValue(), accumulationConfig));
		}
		return r;
	}
	@SuppressWarnings("unchecked")
	@Contract(pure = true)
	public static <T extends Identifiable> Map<IdentifierFragment, List<TimestampedPacket<T>>> mapPackets(Class<T> clazz, List<? extends FragmentedPacketGroup> packetGroups) {
		Map<IdentifierFragment, List<TimestampedPacket<T>>> packetMap = new HashMap<>();
		for (FragmentedPacketGroup packetGroup : packetGroups) {
			for (Packet packet : packetGroup.getPackets()) {
				if (clazz.isInstance(packet)) {
					int fragmentId = packetGroup.getFragmentId(packet);
					T t = (T) packet;
					IdentifierFragment identifierFragment = IdentifierFragment.create(fragmentId, t.getIdentifier());
					List<TimestampedPacket<T>> packetList = packetMap.computeIfAbsent(identifierFragment, k -> new ArrayList<>());
					long dateMillis = packetGroup.getDateMillisOrKnown(packet);
					packetList.add(new TimestampedPacket<>(t, dateMillis));
				}
			}
		}
		return packetMap;
	}
}

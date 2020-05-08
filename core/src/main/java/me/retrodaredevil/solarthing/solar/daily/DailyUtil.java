package me.retrodaredevil.solarthing.solar.daily;

import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.TimestampedPacket;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;
import me.retrodaredevil.solarthing.packets.identification.IdentifierFragment;
import me.retrodaredevil.solarthing.solar.common.DailyData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("Java8MapApi") // needs to compatible with Android SDK 19
public final class DailyUtil {
	private DailyUtil(){ throw new UnsupportedOperationException(); }
	private static <T extends DailyData> DailyPair<T> createDailyPair(boolean isFirst, TimestampedPacket<T> firstPacket, TimestampedPacket<T> endPacket, DailyConfig dailyConfig) {
		final DailyPair.StartPacketType startPacketType;
		if (isFirst) {
			if (firstPacket.getDateMillis() < dailyConfig.getCutOffIfStartBeforeDateMillis() || endPacket.getDateMillis() < dailyConfig.getCutOffIfEndBeforeDateMillis()) {
				startPacketType = DailyPair.StartPacketType.CUT_OFF;
			} else {
				startPacketType = DailyPair.StartPacketType.MIDDLE_OF_DAY_CONNECT;
			}
		} else {
			startPacketType = DailyPair.StartPacketType.FIRST_AFTER_RESET;
		}
		return new DailyPair<>(firstPacket, endPacket, startPacketType);
	}
	public static <T extends DailyData> List<DailyPair<T>> getDailyPairs(List<TimestampedPacket<T>> packets, DailyConfig dailyConfig) {
		List<DailyPair<T>> r = new ArrayList<>();
		TimestampedPacket<T> firstPacket = null;
		TimestampedPacket<T> lastPacket = null;
		for (TimestampedPacket<T> packet : packets) {
			if (firstPacket == null) {
				firstPacket = packet;
			}
			if (lastPacket != null) {
				if (packet.getPacket().isNewDay(lastPacket.getPacket())) {
					r.add(createDailyPair(r.isEmpty(), firstPacket, lastPacket, dailyConfig));
					firstPacket = packet;
				}
			}
			lastPacket = packet;
		}
		if (firstPacket != null) {
			//noinspection ConstantConditions
			assert lastPacket != null;
			r.add(createDailyPair(r.isEmpty(), firstPacket, lastPacket, dailyConfig));
		}
		return r;
	}
	public static <T extends DailyData> Map<IdentifierFragment, List<DailyPair<T>>> getDailyPairs(Map<IdentifierFragment, List<TimestampedPacket<T>>> packetMap, DailyConfig dailyConfig) {
		Map<IdentifierFragment, List<DailyPair<T>>> r = new HashMap<>(packetMap.size());
		for (Map.Entry<IdentifierFragment, List<TimestampedPacket<T>>> entry : packetMap.entrySet()) {
			r.put(entry.getKey(), getDailyPairs(entry.getValue(), dailyConfig));
		}
		return r;
	}
	@SuppressWarnings("unchecked")
	public static <T extends Identifiable> Map<IdentifierFragment, List<TimestampedPacket<T>>> mapPackets(Class<T> clazz, List<FragmentedPacketGroup> packetGroups) {
		Map<IdentifierFragment, List<TimestampedPacket<T>>> packetMap = new HashMap<>();
		for (FragmentedPacketGroup packetGroup : packetGroups) {
			for (Packet packet : packetGroup.getPackets()) {
				if (clazz.isInstance(packet)) {
					Integer fragmentId = packetGroup.getFragmentId(packet);
					T t = (T) packet;
					IdentifierFragment identifierFragment = IdentifierFragment.create(fragmentId, t.getIdentifier());
					List<TimestampedPacket<T>> packetList = packetMap.get(identifierFragment);
					if (packetList == null) {
						packetList = new ArrayList<>();
						packetMap.put(identifierFragment, packetList);
					}
					Long dateMillis = packetGroup.getDateMillis(packet);
					if (dateMillis == null) {
						dateMillis = packetGroup.getDateMillis();
					}
					packetList.add(new TimestampedPacket<>(t, dateMillis));
				}
			}
		}
		return packetMap;
	}
}

package me.retrodaredevil.solarthing.type.alter.flag;

import me.retrodaredevil.solarthing.annotations.UtilityClass;
import me.retrodaredevil.solarthing.type.alter.AlterPacket;
import me.retrodaredevil.solarthing.type.alter.StoredAlterPacket;
import me.retrodaredevil.solarthing.type.alter.packets.FlagPacket;

import java.time.Instant;
import java.util.Objects;
import java.util.stream.Stream;

@UtilityClass
public class FlagUtil {
	private FlagUtil() { throw new UnsupportedOperationException(); }

	public static boolean isFlagActive(Instant now, String flagName, Stream<? extends StoredAlterPacket> packetStream) {
		return packetStream.anyMatch(storedAlterPacket -> {
			AlterPacket alterPacket = storedAlterPacket.getPacket();
			if (alterPacket instanceof FlagPacket) {
				FlagPacket flagPacket = (FlagPacket) alterPacket;
				FlagData data = flagPacket.getFlagData();
				return data.getFlagName().equals(flagName) && data.getActivePeriod().isActive(now);
			}
			return false;
		});
	}
	public static Stream<FlagPacket> mapToFlagPackets(Stream<? extends StoredAlterPacket> packetStream) {
		return packetStream
				.map(storedAlterPacket -> {
					AlterPacket alterPacket = storedAlterPacket.getPacket();
					if (alterPacket instanceof FlagPacket) {
						return (FlagPacket) alterPacket;
					}
					return null;
				})
				.filter(Objects::nonNull)
				;
	}
	public static <T extends FlagPacket> Stream<T> filterActivePackets(Instant now, Stream<T> flagPacketStream) {
		return flagPacketStream.filter(flagPacket -> flagPacket.getFlagData().getActivePeriod().isActive(now));
	}
}

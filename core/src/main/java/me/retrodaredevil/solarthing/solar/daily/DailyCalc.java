package me.retrodaredevil.solarthing.solar.daily;

import me.retrodaredevil.solarthing.solar.common.DailyData;

import java.util.Collection;
import java.util.List;

public final class DailyCalc {
	private DailyCalc() { throw new UnsupportedOperationException(); }

	public static <T extends DailyData> float getSumTotal(Collection<? extends List<DailyPair<T>>> dailyPairListCollection, TotalGetter<T> totalGetter) {
		float total = 0;
		for (List<DailyPair<T>> dailyPairs : dailyPairListCollection) {
			total += getTotal(dailyPairs, totalGetter);
		}
		return total;
	}
	public static <T extends DailyData> float getTotal(List<? extends DailyPair<T>> dailyPairs, TotalGetter<T> totalGetter) {
		float total = 0;
		for (DailyPair<T> dailyPair : dailyPairs) {
			if (dailyPair.getStartPacketType() == DailyPair.StartPacketType.CUT_OFF) {
				total += totalGetter.getTotal(dailyPair.getLatestPacket().getPacket()) - totalGetter.getTotal(dailyPair.getStartPacket().getPacket());
			} else {
				total += totalGetter.getTotal(dailyPair.getLatestPacket().getPacket());
			}
		}
		return total;
	}
	@FunctionalInterface
	public interface TotalGetter<T> {
		float getTotal(T t);
	}
}

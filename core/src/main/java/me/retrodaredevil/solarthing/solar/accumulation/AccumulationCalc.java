package me.retrodaredevil.solarthing.solar.accumulation;

import me.retrodaredevil.solarthing.annotations.UtilityClass;
import me.retrodaredevil.solarthing.packets.TimestampedPacket;
import me.retrodaredevil.solarthing.solar.common.DailyData;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@UtilityClass
public final class AccumulationCalc {
	private AccumulationCalc() { throw new UnsupportedOperationException(); }

	@Contract(pure = true)
	public static <T extends DailyData> float getSumTotal(Collection<? extends List<? extends AccumulationPair<? extends T>>> dailyPairListCollection, TotalGetter<? super T> totalGetter) {
		float total = 0;
		for (List<? extends AccumulationPair<? extends T>> dailyPairs : dailyPairListCollection) {
			total += getTotal(dailyPairs, totalGetter);
		}
		return total;
	}
	@Contract(pure = true)
	public static <T extends DailyData> float getTotal(List<? extends AccumulationPair<? extends T>> accumulationPairs, TotalGetter<? super T> totalGetter) {
		float total = 0;
		for (AccumulationPair<? extends T> accumulationPair : accumulationPairs) {
			if (accumulationPair.getStartPacketType() == AccumulationPair.StartPacketType.CUT_OFF) {
				total += totalGetter.getTotal(accumulationPair.getLatestPacket().getPacket()) - totalGetter.getTotal(accumulationPair.getStartPacket().getPacket());
			} else {
				total += totalGetter.getTotal(accumulationPair.getLatestPacket().getPacket());
			}
		}
		return total;
	}

	/**
	 *
	 * @param accumulationPairs The list of accumulation pairs
	 * @param totalGetter The getter function that gives the desired data point to be summed
	 * @param packets For each packet, an associated {@link SumNode} is present in the returned list
	 * @param <T> The type of the packet that is being used to calculate the total
	 * @return
	 */
	@Contract(pure = true)
	public static <T extends DailyData> List<SumNode> getTotals(List<? extends AccumulationPair<T>> accumulationPairs, TotalGetter<? super T> totalGetter, List<? extends TimestampedPacket<T>> packets) {
		if (accumulationPairs.isEmpty()) {
			throw new IllegalArgumentException("dailyPairs is empty!");
		}
		List<SumNode> r = new ArrayList<>();
		for (TimestampedPacket<T> packet : packets) {
			long dateMillis = packet.getDateMillis();
			List<AccumulationPair<T>> previousAccumulationPairs = new ArrayList<>();
			for (AccumulationPair<T> element : accumulationPairs) {
				previousAccumulationPairs.add(element);
				if (dateMillis <= element.getLatestPacket().getDateMillis()) {
					break;
				}
			}
			if (previousAccumulationPairs.isEmpty()) {
				throw new AssertionError("We checked to make sure dailyPairs is not empty, so why is previousDailyPairs empty?");
			}
			AccumulationPair<T> lastAccumulationPair = previousAccumulationPairs.get(previousAccumulationPairs.size() - 1);
			previousAccumulationPairs.set(previousAccumulationPairs.size() - 1, new AccumulationPair<>(lastAccumulationPair.getStartPacket(), packet, lastAccumulationPair.getStartPacketType()));
			float sum = getTotal(previousAccumulationPairs, totalGetter);
			r.add(new SumNode(sum, dateMillis));
		}
		return r;
	}
	@FunctionalInterface
	public interface TotalGetter<T> {
		float getTotal(T t);
	}
	public static final class SumNode {
		private final float sum;
		private final long dateMillis;

		public SumNode(float sum, long dateMillis) {
			this.sum = sum;
			this.dateMillis = dateMillis;
		}

		public float getSum() {
			return sum;
		}

		public long getDateMillis() {
			return dateMillis;
		}
	}
}

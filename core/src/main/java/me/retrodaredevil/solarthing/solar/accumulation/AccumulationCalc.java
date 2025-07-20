package me.retrodaredevil.solarthing.solar.accumulation;

import me.retrodaredevil.solarthing.annotations.UtilityClass;
import me.retrodaredevil.solarthing.packets.TimestampedPacket;
import me.retrodaredevil.solarthing.solar.accumulation.value.AccumulationValue;
import me.retrodaredevil.solarthing.solar.accumulation.value.AccumulationValueFactory;
import me.retrodaredevil.solarthing.solar.common.DailyData;
import org.jetbrains.annotations.Contract;

import javax.annotation.CheckReturnValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@UtilityClass
public final class AccumulationCalc {
	private AccumulationCalc() { throw new UnsupportedOperationException(); }

	@CheckReturnValue
	@Contract(pure = true)
	public static <T extends DailyData, U extends AccumulationValue<U>> U getSumTotal(
			Collection<? extends List<? extends AccumulationPair<? extends T>>> dailyPairListCollection,
			TotalGetter<T, U> totalGetter, AccumulationValueFactory<U> accumulationValueFactory) {
		U total = accumulationValueFactory.getZero();
		for (List<? extends AccumulationPair<? extends T>> dailyPairs : dailyPairListCollection) {
			U addAmount = getTotal(dailyPairs, totalGetter, accumulationValueFactory);
			total = total.plus(addAmount);
		}
		return total;
	}
	@CheckReturnValue
	@Contract(pure = true)
	public static <T extends DailyData, U extends AccumulationValue<U>> U getTotal(
			List<? extends AccumulationPair<? extends T>> accumulationPairs, TotalGetter<T, U> totalGetter,
			AccumulationValueFactory<U> accumulationValueFactory) {
		U total = accumulationValueFactory.getZero();
		for (AccumulationPair<? extends T> accumulationPair : accumulationPairs) {
			final U addAmount;
			if (accumulationPair.getStartPacketType() == AccumulationPair.StartPacketType.CUT_OFF) {
				addAmount = totalGetter.getTotal(accumulationPair.getLatestPacket().getPacket()).minus(totalGetter.getTotal(accumulationPair.getStartPacket().getPacket()));
			} else {
				addAmount = totalGetter.getTotal(accumulationPair.getLatestPacket().getPacket());
			}
			total = total.plus(addAmount);
		}
		return total;
	}

	/**
	 *
	 * @param accumulationPairs The list of accumulation pairs
	 * @param totalGetter The getter function that gives the desired data point to be summed
	 * @param packets For each packet, an associated {@link SumNode} is present in the returned list
	 * @param <T> The type of the packet that is being used to calculate the total
	 */
	@CheckReturnValue
	@Contract(pure = true)
	public static <T extends DailyData, U extends AccumulationValue<U>> List<SumNode<U>> getTotals(
			List<? extends AccumulationPair<T>> accumulationPairs, TotalGetter<T, U> totalGetter, List<? extends TimestampedPacket<T>> packets,
			AccumulationValueFactory<U> accumulationValueFactory) {
		if (accumulationPairs.isEmpty()) {
			throw new IllegalArgumentException("dailyPairs is empty!");
		}
		List<SumNode<U>> r = new ArrayList<>();
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
			U sum = getTotal(previousAccumulationPairs, totalGetter, accumulationValueFactory);
			r.add(new SumNode<>(sum, dateMillis));
		}
		return r;
	}

	public static final class SumNode<U extends AccumulationValue<U>> {
		private final U sum;
		private final long dateMillis;

		public SumNode(U sum, long dateMillis) {
			this.sum = sum;
			this.dateMillis = dateMillis;
		}

		public U getSum() {
			return sum;
		}

		public long getDateMillis() {
			return dateMillis;
		}
	}
}

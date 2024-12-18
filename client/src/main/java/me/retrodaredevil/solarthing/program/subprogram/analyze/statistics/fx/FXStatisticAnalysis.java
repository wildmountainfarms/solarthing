package me.retrodaredevil.solarthing.program.subprogram.analyze.statistics.fx;

import com.google.common.math.Stats;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.InstancePacketGroup;
import me.retrodaredevil.solarthing.program.subprogram.analyze.statistics.CommonPercentiles;
import me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPacket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class FXStatisticAnalysis {
	private FXStatisticAnalysis() { throw new UnsupportedOperationException(); }

	/**
	 *
	 * @param packets A list of packet groups, where each packet group must have at least one FX packet.
	 * @return A map of Outback address to {@link FXStatisticCollection} for that given FX
	 */
	public static Map<Integer, FXStatisticCollection> analyze(List<InstancePacketGroup> packets) {
		Map<Integer, FXStatistic<List<Number>>> accumulators = new HashMap<>();
		for (InstancePacketGroup packetGroup : packets) {
			for (Packet packet : packetGroup.getPackets()) {
				if (packet instanceof FXStatusPacket fx) {
					int address = fx.getAddress();
					FXStatistic<List<Number>> accumulator = accumulators.computeIfAbsent(address, _address -> FXStatistic.initUsing(ArrayList::new));
					accumulator.apply(packetToStatistic(fx), List::add);
				}
			}
		}
		Map<Integer, FXStatisticCollection> statsMap = new HashMap<>();
		for (Map.Entry<Integer, FXStatistic<List<Number>>> entry : accumulators.entrySet()) {
			FXStatistic<Stats> stats = entry.getValue().convert(Stats::of);
			FXStatistic<CommonPercentiles> percentiles = entry.getValue().convert(CommonPercentiles::fromDataset);

			statsMap.put(entry.getKey(), new FXStatisticCollection(stats, percentiles));
		}
		return statsMap;
	}

	private static FXStatistic<Number> packetToStatistic(FXStatusPacket packet) {
		return new FXStatistic<>(
				packet.getBatteryVoltage(),

				packet.getInverterCurrentRaw(),
				packet.getInverterWattage(),

				packet.getChargerCurrentRaw(),
				packet.getChargerWattage(),

				packet.getBuyCurrentRaw(),
				packet.getBuyWattage(),

				packet.getSellCurrentRaw(),
				packet.getSellWattage(),

				packet.getInputVoltageRaw(),
				packet.getOutputVoltageRaw()
		);
	}
}

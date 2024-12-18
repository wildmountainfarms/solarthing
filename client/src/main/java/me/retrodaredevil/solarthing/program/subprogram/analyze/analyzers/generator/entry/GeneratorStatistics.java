package me.retrodaredevil.solarthing.program.subprogram.analyze.analyzers.generator.entry;

import me.retrodaredevil.solarthing.program.subprogram.analyze.statistics.fx.FXStatisticCollection;
import me.retrodaredevil.solarthing.solar.outback.OutbackIdentifier;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;

/**
 * Represents statistics of some time period of a generator run, not necessarily the entire generator run
 */
public record GeneratorStatistics(
		Instant startTime,
		Instant endTime,
//		float averageBatteryVoltage,
//		float batteryVoltageStandardDeviation,
		Map<Integer, FXStatisticCollection> fxStatistics
) {
	public Collection<Integer> getFXAddresses() {
		return fxStatistics.keySet();
	}
	public FXStatisticCollection getStatistics(int fxAddress) {
		FXStatisticCollection r = fxStatistics.get(fxAddress);
		if (r == null) {
			throw new IllegalArgumentException("Unknown FX address: " + fxAddress);
		}
		return r;
	}
	public FXStatisticCollection getStatistics(OutbackIdentifier outbackIdentifier) {
		return getStatistics(outbackIdentifier.getAddress());
	}
}

package me.retrodaredevil.solarthing.program.subprogram.analyze.analyzers.generator.entry;

import java.time.Instant;


/**
 * Represents a single generator run and statistics associated with that run.
 * <p>
 * TODO:
 * <ul>
 *     <li>Standard deviation, percentiles (5th and 95th), duration of extremes, moving average</li>
 *     <li>Statistics on only the first hour of the run</li>
 * </ul>
 *
 * @param startTime The time of the first packet showing the generator starting
 * @param endTime The time of the last packet that the generator was running for
 */
public record GeneratorRunEntry(
		Instant startTime,
		Instant endTime,
		GeneratorStatistics wholeStatistics
//		GeneratorStatistics acUseStatistics,
//		List<GeneratorStatistics> acUseStatisticList,
//		List<GeneratorStatistics> acDropStatistics,
//		Map<OperationalMode, List<GeneratorStatistics>> operationalModeToStatisticsMap
) {
}

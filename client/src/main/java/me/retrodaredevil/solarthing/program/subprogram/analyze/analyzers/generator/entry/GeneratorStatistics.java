package me.retrodaredevil.solarthing.program.subprogram.analyze.analyzers.generator.entry;

import java.time.Instant;

/**
 * Represents statistics of some time period of a generator run, not necessarily the entire generator run
 *
 * @deprecated Not yet implemented
 */
@Deprecated
public record GeneratorStatistics(
		Instant startTime,
		Instant endTime,
		float averageBatteryVoltage,
		float average
) {}

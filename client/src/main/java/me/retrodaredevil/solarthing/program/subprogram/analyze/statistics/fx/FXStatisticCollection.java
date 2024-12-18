package me.retrodaredevil.solarthing.program.subprogram.analyze.statistics.fx;

import com.google.common.math.Stats;
import me.retrodaredevil.solarthing.program.subprogram.analyze.statistics.CommonPercentiles;

public record FXStatisticCollection(
		FXStatistic<Stats> stats,
		FXStatistic<CommonPercentiles> percentiles
) {
}

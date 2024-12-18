package me.retrodaredevil.solarthing.program.subprogram.analyze.statistics;

import com.google.common.math.Quantiles;

import java.util.List;
import java.util.Map;

public record CommonPercentiles(
		double p0_1, double p1, double p10,
		double p90, double p99, double p99_9
) {
	private static final Quantiles.ScaleAndIndexes PERCENTILE_1000 = Quantiles.scale(1000).indexes(1, 10, 100, 900, 990, 999);

	public static CommonPercentiles fromDataset(List<? extends Number> dataset) {
		Map<Integer, Double> result = PERCENTILE_1000.compute(dataset);
		return new CommonPercentiles(
				result.get(1), result.get(10), result.get(100),
				result.get(900), result.get(990), result.get(999)
		);
	}

}

package me.retrodaredevil.solarthing.datasource.endpoint.query;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import me.retrodaredevil.solarthing.datasource.endpoint.DataPoint;

import java.util.List;

@JsonSerialize
public class TimeSeriesResult implements QueryResult {
	@JsonSerialize
	private final String target;
	@SuppressWarnings("SpellCheckingInspection")
	@JsonSerialize
	private final List<DataPoint> datapoints;

	public TimeSeriesResult(String target, List<DataPoint> datapoints) {
		this.target = target;
		this.datapoints = datapoints;
	}
}

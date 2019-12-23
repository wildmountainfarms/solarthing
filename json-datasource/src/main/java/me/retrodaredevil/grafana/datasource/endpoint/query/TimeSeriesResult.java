package me.retrodaredevil.grafana.datasource.endpoint.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import me.retrodaredevil.grafana.datasource.endpoint.DataPoint;

import java.util.List;

@JsonSerialize
public class TimeSeriesResult implements QueryResult {
	@JsonSerialize
	private final String target;
	@SuppressWarnings("SpellCheckingInspection")
	@JsonSerialize @JsonProperty(value = "datapoints")
	private final List<DataPoint> dataPoints;

	public TimeSeriesResult(String target, List<DataPoint> dataPoints) {
		this.target = target;
		this.dataPoints = dataPoints;
	}
}

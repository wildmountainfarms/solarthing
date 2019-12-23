package me.retrodaredevil.grafana.datasource.endpoint.query;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.grafana.datasource.endpoint.Range;
import me.retrodaredevil.grafana.datasource.endpoint.Target;

import java.util.List;

//@JsonIgnoreProperties({ "range", "requestId", "timezone", "panelId", "dashboardId", "timeInfo", "interval", "scopedVars", "cacheTimeout", "startTime", "rangeRaw", "adhocFilters" })
@JsonIgnoreProperties(ignoreUnknown = true)
public class QueryRequest {
	@JsonDeserialize
	private Range range;
	@JsonDeserialize
	private long intervalMs;
	@JsonDeserialize
	private List<Target> targets;
	@JsonDeserialize
	private String format;
	@JsonDeserialize
	private int maxDataPoints;

	public Range getRange() {
		return range;
	}

	public long getIntervalMs() {
		return intervalMs;
	}

	public List<Target> getTargets() {
		return targets;
	}

	public String getFormat() {
		return format;
	}

	public int getMaxDataPoints() {
		return maxDataPoints;
	}

	@Override
	public String toString() {
		String format = this.format == null ? "null" : "'" + this.format + "'";
		return "QueryRequest(" +
				"range=" + range +
				", intervalMs=" + intervalMs +
				", targets=" + targets +
				", format=" + format +
				", maxDataPoints=" + maxDataPoints +
				')';
	}
}

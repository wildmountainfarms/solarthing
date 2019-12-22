package me.retrodaredevil.solarthing.datasource.endpoint.query;

import me.retrodaredevil.solarthing.datasource.endpoint.Range;
import me.retrodaredevil.solarthing.datasource.endpoint.Target;

import java.util.List;

public class QueryRequest {
	private Range range;
	private long intervalMs;
	private List<Target> targets;
	private String format;
	private int maxDataPoints;
}

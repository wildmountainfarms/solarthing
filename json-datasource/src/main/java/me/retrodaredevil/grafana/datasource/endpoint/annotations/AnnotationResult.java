package me.retrodaredevil.grafana.datasource.endpoint.annotations;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

public class AnnotationResult {
	@JsonSerialize
	private final String text;
	@JsonSerialize
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private final String title;
	@JsonSerialize
	private final boolean isRegion;
	@JsonSerialize
	private final long time;
	@JsonSerialize
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private final Long timeEnd;
	@JsonSerialize
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private final List<String> tags;

	public AnnotationResult(String text, String title, boolean isRegion, long time, Long timeEnd, List<String> tags) {
		this.text = text;
		this.title = title;
		this.isRegion = isRegion;
		this.time = time;
		this.timeEnd = timeEnd;
		this.tags = tags;
	}
}

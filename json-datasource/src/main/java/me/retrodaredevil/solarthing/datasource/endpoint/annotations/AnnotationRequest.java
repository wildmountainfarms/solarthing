package me.retrodaredevil.solarthing.datasource.endpoint.annotations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.datasource.endpoint.Range;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AnnotationRequest {
	@JsonDeserialize
	private final Range range;
	@JsonDeserialize
	private final Annotation annotation;

	private AnnotationRequest(){
		this(null, null);
	}

	public AnnotationRequest(Range range, Annotation annotation) {
		this.range = range;
		this.annotation = annotation;
	}

	@Override
	public String toString() {
		return "AnnotationRequest(" +
				"range=" + range +
				", annotation=" + annotation +
				')';
	}
}

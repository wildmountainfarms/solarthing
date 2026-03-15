package me.retrodaredevil.solarthing.solcast.common;

import org.jspecify.annotations.NullMarked;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


@NullMarked
public final class EstimatedActualResult {
	private final List<EstimatedActual> estimatedActuals;

	@JsonCreator
	public EstimatedActualResult(@JsonProperty("estimated_actuals") List<EstimatedActual> estimatedActuals) {
		this.estimatedActuals = estimatedActuals;
	}

	public List<EstimatedActual> getEstimatedActuals() {
		return estimatedActuals;
	}
}

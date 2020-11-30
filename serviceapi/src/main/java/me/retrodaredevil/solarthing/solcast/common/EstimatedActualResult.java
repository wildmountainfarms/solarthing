package me.retrodaredevil.solarthing.solcast.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

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

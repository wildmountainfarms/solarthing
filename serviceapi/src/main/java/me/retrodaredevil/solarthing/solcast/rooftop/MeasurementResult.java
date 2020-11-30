package me.retrodaredevil.solarthing.solcast.rooftop;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public final class MeasurementResult {
	private final Measurement measurement;
	private final List<Measurement> measurements;

	private final String resourceId;
	@JsonCreator
	private MeasurementResult(
			@JsonProperty("measurement") Measurement measurement, @JsonProperty("measurements") List<Measurement> measurements,
			@JsonProperty(value = "site_resource_id", required = true) String resourceId
	) {
		this.measurement = measurement;
		this.measurements = measurements;
		this.resourceId = resourceId;
	}

	@JsonProperty("site_resource_id")
	public String getResourceId() {
		return resourceId;
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("measurement")
	public Measurement getMeasurement() {
		return measurement;
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("measurements")
	public List<Measurement> getMeasurements() {
		return measurements;
	}
}

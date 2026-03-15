package me.retrodaredevil.solarthing.solcast.rooftop;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public final class MeasurementResult {
	private final @Nullable Measurement measurement;
	private final @Nullable List<Measurement> measurements;

	private final String resourceId;
	@JsonCreator
	private MeasurementResult(
			@JsonProperty("measurement") @Nullable Measurement measurement, @JsonProperty("measurements") @Nullable List<Measurement> measurements,
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
	public @Nullable Measurement getMeasurement() {
		return measurement;
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("measurements")
	public @Nullable List<Measurement> getMeasurements() {
		return measurements;
	}
}

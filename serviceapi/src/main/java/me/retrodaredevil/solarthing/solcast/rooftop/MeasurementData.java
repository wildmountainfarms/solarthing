package me.retrodaredevil.solarthing.solcast.rooftop;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

public final class MeasurementData {
	private final Measurement measurement;
	private final List<Measurement> measurements;

	@JsonCreator
	private MeasurementData(@JsonProperty("measurement") Measurement measurement, @JsonProperty("measurements") List<Measurement> measurements) {
		this.measurement = measurement;
		this.measurements = measurements;
	}

	public static MeasurementData createSingle(Measurement measurement) {
		return new MeasurementData(requireNonNull(measurement), null);
	}
	public static MeasurementData createMultiple(List<Measurement> measurements) {
		return new MeasurementData(null, new ArrayList<>(measurements));
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

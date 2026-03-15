package me.retrodaredevil.solarthing.solcast.common;

import org.jspecify.annotations.NullMarked;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.NonNull;

import java.util.List;

import static java.util.Objects.requireNonNull;


@NullMarked
public final class ForecastResult {
	private final List<Forecast> forecasts;

	@JsonCreator
	public ForecastResult(@JsonProperty(value = "forecasts", required = true) List<Forecast> forecasts) {
		this.forecasts = requireNonNull(forecasts);
	}

	public @NonNull List<Forecast> getForecasts() {
		return forecasts;
	}
}

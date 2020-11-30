package me.retrodaredevil.solarthing.solcast.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.NotNull;

import java.util.List;

import static java.util.Objects.requireNonNull;

public final class ForecastResult {
	private final List<Forecast> forecasts;

	@JsonCreator
	public ForecastResult(@JsonProperty(value = "forecasts", required = true) List<Forecast> forecasts) {
		requireNonNull(this.forecasts = forecasts);
	}

	public @NotNull List<Forecast> getForecasts() {
		return forecasts;
	}
}

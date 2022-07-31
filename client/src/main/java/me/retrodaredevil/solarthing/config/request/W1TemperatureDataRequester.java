package me.retrodaredevil.solarthing.config.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.misc.weather.W1TemperatureListUpdater;

import java.io.File;

@JsonTypeName("w1-temperature")
public class W1TemperatureDataRequester implements DataRequester {
	private final File directory;
	private final int dataId;

	@JsonCreator
	public W1TemperatureDataRequester(
			@JsonProperty(value = "directory", required = true) File directory,
			@JsonProperty(value = "data_id", required = true) int dataId
	) {
		this.directory = directory;
		this.dataId = dataId;
		// TODO add required property
	}

	@Override
	public DataRequesterResult create(RequestObject requestObject) {
		return DataRequesterResult.builder()
				.statusPacketListReceiver(new W1TemperatureListUpdater(directory, dataId))
				.build();
	}
}

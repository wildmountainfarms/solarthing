package me.retrodaredevil.solarthing.config.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.misc.weather.W1TemperatureListUpdater;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;

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
	}

	@Override
	public PacketListReceiver createPacketListReceiver(PacketListReceiver eventPacketReceiver) {
		return new W1TemperatureListUpdater(directory, dataId);
	}
}

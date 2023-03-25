package me.retrodaredevil.solarthing.config.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.misc.device.CpuTemperatureListUpdater;

@JsonTypeName("cpu-temp")
public class CpuTemperatureDataRequester implements DataRequester {
	private final DataRequesterResult result;

	@JsonCreator
	public CpuTemperatureDataRequester(@JsonProperty(value = "processors", required = true) int processorCount) {
		result = DataRequesterResult.builder()
				.statusPacketListReceiver(new CpuTemperatureListUpdater(processorCount))
				.build();
	}

	@Override
	public DataRequesterResult create(RequestObject requestObject) {
		return result;
	}
}

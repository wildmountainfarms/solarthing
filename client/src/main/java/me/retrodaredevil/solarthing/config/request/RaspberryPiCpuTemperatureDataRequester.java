package me.retrodaredevil.solarthing.config.request;

import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.misc.device.RaspberryPiCpuTemperatureListUpdater;

@JsonTypeName("rpi-cpu-temp")
public class RaspberryPiCpuTemperatureDataRequester implements DataRequester {
	private final DataRequesterResult result = DataRequesterResult.builder()
			.statusPacketListReceiver(new RaspberryPiCpuTemperatureListUpdater())
			.build();
	@Override
	public DataRequesterResult create(RequestObject requestObject) {
		return result;
	}
}

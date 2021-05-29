package me.retrodaredevil.solarthing.config.request;

import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.misc.device.RaspberryPiCpuTemperatureListUpdater;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;

@JsonTypeName("rpi-cpu-temp")
public class RaspberryPiCpuTemperatureDataRequester implements DataRequester {
	private final DataRequesterResult result = new DataRequesterResult(new RaspberryPiCpuTemperatureListUpdater());
	@Override
	public DataRequesterResult create(RequestObject requestObject) {
		return result;
	}
}

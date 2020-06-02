package me.retrodaredevil.solarthing.config.request;

import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.misc.device.RaspberryPiCpuTemperatureListUpdater;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;

@JsonTypeName("rpi-cpu-temp")
public class RaspberryPiCpuTemperatureDataRequester implements DataRequester {
	private final PacketListReceiver packetListReceiver = new RaspberryPiCpuTemperatureListUpdater();
	@Override
	public PacketListReceiver createPacketListReceiver(PacketListReceiver eventPacketReceiver) {
		return packetListReceiver;
	}
}

package me.retrodaredevil.solarthing.config.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;

@JsonSubTypes({
		@JsonSubTypes.Type(RaspberryPiCpuTemperatureDataRequester.class),
		@JsonSubTypes.Type(W1TemperatureDataRequester.class),
})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public interface DataRequester {

	PacketListReceiver createPacketListReceiver(PacketListReceiver eventPacketReceiver);

}

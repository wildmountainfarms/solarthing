package me.retrodaredevil.solarthing.message.event;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import me.retrodaredevil.solarthing.message.MessageSender;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;

@JsonSubTypes({
		@JsonSubTypes.Type(LowBatteryVoltageEvent.class),
		@JsonSubTypes.Type(FXOperationalModeChangeEvent.class),
		@JsonSubTypes.Type(ACModeChangeEvent.class),
		@JsonSubTypes.Type(TemperatureEvent.class),
})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
public interface MessageEvent {
	void run(MessageSender sender, FragmentedPacketGroup previous, FragmentedPacketGroup current);
}

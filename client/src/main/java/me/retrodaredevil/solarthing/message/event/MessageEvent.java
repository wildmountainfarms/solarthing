package me.retrodaredevil.solarthing.message.event;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import me.retrodaredevil.solarthing.message.MessageSender;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.packets.collection.InstancePacketGroup;

@JsonSubTypes({
		@JsonSubTypes.Type(LowBatteryVoltageEvent.class),
		@JsonSubTypes.Type(FXOperationalModeChangeEvent.class),
		@JsonSubTypes.Type(ACModeChangeEvent.class),
		@JsonSubTypes.Type(TemperatureEvent.class),
		@JsonSubTypes.Type(MXFloatModeStuckEvent.class),
		@JsonSubTypes.Type(LowACInputEvent.class),
		@JsonSubTypes.Type(ACModeAlertEvent.class),
		@JsonSubTypes.Type(GeneratorStateEvent.class),
		@JsonSubTypes.Type(GeneratorUnusedAlertEvent.class),
		@JsonSubTypes.Type(ExecutionFeedbackEvent.class),
		@JsonSubTypes.Type(NoHeartbeatEvent.class),
})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
public interface MessageEvent {
	default void run(MessageSender sender, FragmentedPacketGroup previous, FragmentedPacketGroup current) {}
	default void runForEvent(MessageSender sender, InstancePacketGroup packetGroup) {}
}

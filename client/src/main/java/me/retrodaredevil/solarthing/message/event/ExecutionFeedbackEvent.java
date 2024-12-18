package me.retrodaredevil.solarthing.message.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.message.MessageSender;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.InstancePacketGroup;
import me.retrodaredevil.solarthing.type.event.feedback.ExecutionFeedbackPacket;

@JsonTypeName("feedback")
public class ExecutionFeedbackEvent implements MessageEvent {
	@JsonCreator
	public ExecutionFeedbackEvent() {
	}

	@Override
	public void runForEvent(MessageSender sender, InstancePacketGroup packetGroup) {
		for (Packet packet : packetGroup.getPackets()) {
			if (packet instanceof ExecutionFeedbackPacket executionFeedbackPacket) {
				sender.sendMessage(executionFeedbackPacket.getMessage());
			}
		}
	}
}

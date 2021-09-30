package me.retrodaredevil.solarthing.message.event;

import me.retrodaredevil.solarthing.message.MessageSender;
import me.retrodaredevil.solarthing.packets.collection.InstancePacketGroup;

public interface EventMessageEvent extends MessageEvent {
	void run(MessageSender sender, InstancePacketGroup packetGroup);
}

package me.retrodaredevil.solarthing.program;

import me.retrodaredevil.solarthing.PacketGroupReceiver;
import me.retrodaredevil.solarthing.commands.packets.open.RequestHeartbeatPacket;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.TargetPacketGroup;
import me.retrodaredevil.solarthing.reason.ExecutionReason;
import me.retrodaredevil.solarthing.reason.OpenSourceExecutionReason;
import me.retrodaredevil.solarthing.type.event.feedback.HeartbeatData;
import me.retrodaredevil.solarthing.type.event.feedback.HeartbeatPacket;
import me.retrodaredevil.solarthing.type.event.feedback.ImmutableHeartbeatPacket;
import me.retrodaredevil.solarthing.type.open.OpenSource;

import java.time.Instant;
import java.util.Arrays;

public class RequestHeartbeatReceiver implements PacketGroupReceiver {
	private final PacketListReceiverHandler eventPacketListReceiverHandler;

	public RequestHeartbeatReceiver(PacketListReceiverHandler eventPacketListReceiverHandler) {
		this.eventPacketListReceiverHandler = eventPacketListReceiverHandler;
	}

	@Override
	public void receivePacketGroup(String sender, TargetPacketGroup packetGroup) {
		Instant now = Instant.now();

		for (Packet packet : packetGroup.getPackets()) {
			if (packet instanceof RequestHeartbeatPacket) {
				RequestHeartbeatPacket requestHeartbeatPacket = (RequestHeartbeatPacket) packet;
				HeartbeatData data = requestHeartbeatPacket.getData();
				OpenSource openSource = new OpenSource(sender, packetGroup.getDateMillis(), requestHeartbeatPacket, requestHeartbeatPacket.getUniqueString());
				ExecutionReason executionReason = new OpenSourceExecutionReason(openSource);
				HeartbeatPacket heartbeatPacket = new ImmutableHeartbeatPacket(data, executionReason);
				eventPacketListReceiverHandler.uploadSimple(now, Arrays.asList(heartbeatPacket));
			}
		}
	}
}

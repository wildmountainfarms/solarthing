package me.retrodaredevil.solarthing.message.event;

import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.message.MessageSender;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.packets.collection.InstancePacketGroup;
import me.retrodaredevil.solarthing.type.event.feedback.HeartbeatData;
import me.retrodaredevil.solarthing.type.event.feedback.HeartbeatPacket;
import me.retrodaredevil.solarthing.util.heartbeat.HeartbeatIdentifier;
import me.retrodaredevil.solarthing.util.heartbeat.HeartbeatNode;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@JsonTypeName("noheartbeat")
public class NoHeartbeatEvent implements MessageEvent {

	private final Map<HeartbeatIdentifier, HeartbeatNode> map = new HashMap<>();

	@Override
	public void run(MessageSender sender, FragmentedPacketGroup previous, FragmentedPacketGroup current) {
		// We aren't using this method to compare status packets, we're just using this method as a hook
		//   that is continuously called.
		checkForAlerts(sender);
	}

	@Override
	public void runForEvent(MessageSender sender, InstancePacketGroup packetGroup) {
		for (Packet packet : packetGroup.getPackets()) {
			if (packet instanceof HeartbeatPacket) {
				HeartbeatPacket heartbeatPacket = (HeartbeatPacket) packet;
				long dateMillis = packetGroup.getDateMillis();
				int fragmentId = packetGroup.getFragmentId();
				HeartbeatIdentifier identifier = new HeartbeatIdentifier(heartbeatPacket.getData().getIdentifier(), fragmentId);
				HeartbeatNode node = new HeartbeatNode(dateMillis, heartbeatPacket);
				map.put(identifier, node);
			}
		}
	}

	private void alertFor(MessageSender messageSender, HeartbeatNode heartbeatNode) {
		messageSender.sendMessage("No heartbeat for: " + heartbeatNode.getHeartbeatPacket().getData().getDisplayName());
	}

	private void checkForAlerts(MessageSender messageSender) {
		long now = System.currentTimeMillis();
		for (Iterator<Map.Entry<HeartbeatIdentifier, HeartbeatNode>> it = map.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry<HeartbeatIdentifier, HeartbeatNode> entry = it.next();
			HeartbeatData data = entry.getValue().getHeartbeatPacket().getData();
			long latestNextHeartbeat = entry.getValue().getDateMillis() + data.getExpectedDurationToNextHeartbeat().toMillis() + data.getBufferDuration().toMillis();
			if (now > latestNextHeartbeat) {
				it.remove();
				alertFor(messageSender, entry.getValue());
			}
		}
	}

}

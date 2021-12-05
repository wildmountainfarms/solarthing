package me.retrodaredevil.solarthing.message.event;

import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.message.MessageSender;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.packets.collection.InstancePacketGroup;
import me.retrodaredevil.solarthing.type.event.feedback.HeartbeatData;
import me.retrodaredevil.solarthing.type.event.feedback.HeartbeatPacket;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

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
		messageSender.sendMessage("No heartbeat for: " + heartbeatNode.heartbeatPacket.getData().getDisplayName());
	}

	private void checkForAlerts(MessageSender messageSender) {
		long now = System.currentTimeMillis();
		for (Iterator<Map.Entry<HeartbeatIdentifier, HeartbeatNode>> it = map.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry<HeartbeatIdentifier, HeartbeatNode> entry = it.next();
			HeartbeatData data = entry.getValue().heartbeatPacket.getData();
			long latestNextHeartbeat = entry.getValue().dateMillis + data.getExpectedDurationToNextHeartbeat().toMillis() + data.getBufferDuration().toMillis();
			if (now > latestNextHeartbeat) {
				it.remove();
				alertFor(messageSender, entry.getValue());
			}
		}
	}

	private static class HeartbeatIdentifier {
		private final String identifier;
		private final int fragmentId;

		private HeartbeatIdentifier(String identifier, int fragmentId) {
			this.identifier = identifier;
			this.fragmentId = fragmentId;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			HeartbeatIdentifier that = (HeartbeatIdentifier) o;
			return fragmentId == that.fragmentId && identifier.equals(that.identifier);
		}

		@Override
		public int hashCode() {
			return Objects.hash(identifier, fragmentId);
		}
	}
	private static class HeartbeatNode {
		private final long dateMillis;
		private final HeartbeatPacket heartbeatPacket;

		private HeartbeatNode(long dateMillis, HeartbeatPacket heartbeatPacket) {
			this.dateMillis = dateMillis;
			this.heartbeatPacket = heartbeatPacket;
		}
	}
}

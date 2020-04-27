package me.retrodaredevil.solarthing.packets.collection.parsing;

import com.fasterxml.jackson.databind.node.ObjectNode;
import me.retrodaredevil.solarthing.packets.collection.PacketGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class PacketParseUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(PacketParseUtil.class);

	public static List<PacketGroup> parseRawPackets(List<? extends ObjectNode> packetNodes, PacketGroupParser parser) {
		List<PacketGroup> rawPacketGroups = new ArrayList<>(packetNodes.size());
		for(ObjectNode object : packetNodes){
			try {
				PacketGroup packetGroup = parser.parse(object);
				rawPacketGroups.add(packetGroup);
			} catch (PacketParseException e) {
				LOGGER.warn("Got PacketParseException. Going to continue...", e);
			}
		}
		return rawPacketGroups;
	}
}

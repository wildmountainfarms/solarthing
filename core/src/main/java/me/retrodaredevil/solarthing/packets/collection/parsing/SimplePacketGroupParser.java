package me.retrodaredevil.solarthing.packets.collection.parsing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.PacketGroup;
import me.retrodaredevil.solarthing.packets.collection.PacketGroups;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class SimplePacketGroupParser implements PacketGroupParser {
	private final JsonPacketParser packetParser;

	public SimplePacketGroupParser(JsonPacketParser packetParser) {
		this.packetParser = requireNonNull(packetParser);
	}

	@Override
	public PacketGroup parse(ObjectNode objectNode) throws PacketParseException {
		JsonNode dateMillisNode = objectNode.get("dateMillis");
		if(dateMillisNode == null){
			throw new PacketParseException("'dateMillis' does not exist for objectNode=" + objectNode);
		}
		if(!dateMillisNode.isNumber()){
			throw new PacketParseException("'dateMillis' is not a number! dateMillisNode=" + dateMillisNode);
		}
		long dateMillis = dateMillisNode.asLong();
		JsonNode packetsNode = objectNode.get("packets");
		if(packetsNode == null){
			throw new PacketParseException("'packets' does not exist for objectNode=" + objectNode);
		}
		if(!packetsNode.isArray()){
			throw new PacketParseException("'packets' is not an array! packetsNode=" + packetsNode);
		}
		List<Packet> packetList = new ArrayList<>();
		for (JsonNode jsonPacket : packetsNode) {
			Packet packet = packetParser.parsePacket(jsonPacket);
			if(packet != null){
				packetList.add(packet);
			}
		}

		return PacketGroups.createPacketGroup(packetList, dateMillis);
	}
}

package me.retrodaredevil.solarthing.packets.collection.parsing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import me.retrodaredevil.solarthing.packets.DocumentedPacket;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.PacketGroup;
import me.retrodaredevil.solarthing.packets.collection.PacketGroups;

import me.retrodaredevil.solarthing.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

public class SimplePacketGroupParser {
	private final ObjectMapper mapper;
	private final PacketParsingErrorHandler errorHandler;

	public SimplePacketGroupParser(ObjectMapper mapper, PacketParsingErrorHandler errorHandler) {
		this.mapper = mapper;
		this.errorHandler = errorHandler;
	}


	public @NotNull PacketGroup parse(ObjectNode objectNode) throws PacketParseException {
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
			DocumentedPacket packet = null;
			try {
				packet = mapper.convertValue(jsonPacket, DocumentedPacket.class);
			} catch (IllegalArgumentException ex) {
				errorHandler.handleError(ex);
			}
			if(packet != null){
				packetList.add(packet);
			}
		}
		return PacketGroups.createPacketGroup(packetList, dateMillis);
	}
}

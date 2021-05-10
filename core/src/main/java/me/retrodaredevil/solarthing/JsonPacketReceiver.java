package me.retrodaredevil.solarthing;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

@Deprecated
public interface JsonPacketReceiver {
	void receivePackets(List<ObjectNode> jsonPackets);
}

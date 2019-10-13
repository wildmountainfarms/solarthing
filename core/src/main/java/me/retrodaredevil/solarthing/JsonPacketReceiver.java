package me.retrodaredevil.solarthing;

import com.google.gson.JsonObject;

import java.util.List;

public interface JsonPacketReceiver {
	void receivePackets(List<JsonObject> jsonPackets);
}

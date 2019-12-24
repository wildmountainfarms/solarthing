package me.retrodaredevil.solarthing;

import com.google.gson.JsonObject;

import java.util.List;

@Deprecated
public interface JsonPacketReceiver {
	void receivePackets(List<JsonObject> jsonPackets);
}

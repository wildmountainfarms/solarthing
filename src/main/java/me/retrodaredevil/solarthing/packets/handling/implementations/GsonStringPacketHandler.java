package me.retrodaredevil.solarthing.packets.handling.implementations;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;

public class GsonStringPacketHandler implements StringPacketHandler {
	private final Gson gson;
	
	public GsonStringPacketHandler(Gson gson) {
		this.gson = gson;
	}
	public GsonStringPacketHandler(){
		this(new GsonBuilder().setPrettyPrinting().serializeNulls().create());
	}
	
	@Override
	public String getString(PacketCollection packetCollection) {
		return gson.toJson(packetCollection);
	}
}

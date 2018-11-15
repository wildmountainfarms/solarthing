package me.retrodaredevil.solarthing.packet;

import com.google.gson.JsonObject;

import java.util.Collection;

public final class PacketCollections {
	private PacketCollections(){ throw new UnsupportedOperationException(); }

	public static PacketCollection createFromPackets(Collection<Packet> packets){
		return new ImmutablePacketCollection(packets);
	}
	public static PacketCollection createFromJson(JsonObject object){
		return new ImmutablePacketCollection(object);
	}
}

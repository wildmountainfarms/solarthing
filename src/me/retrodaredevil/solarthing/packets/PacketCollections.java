package me.retrodaredevil.solarthing.packets;

import com.google.gson.JsonObject;

import java.util.Collection;

public final class PacketCollections {
	private PacketCollections(){ throw new UnsupportedOperationException(); }

	public static PacketCollection createFromPackets(Collection<Packet> packets){
		return new ImmutablePacketCollection(packets);
	}
	public static PacketCollection createFromJson(JsonObject object, JsonPacketGetter packetGetter){
		return new ImmutablePacketCollection(object, packetGetter);
	}
	public interface JsonPacketGetter {
		Packet createFromJson(JsonObject packetObject);
	}
}

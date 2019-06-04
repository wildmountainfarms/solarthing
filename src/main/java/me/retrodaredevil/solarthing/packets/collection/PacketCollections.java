package me.retrodaredevil.solarthing.packets.collection;

import com.google.gson.JsonObject;
import me.retrodaredevil.solarthing.packets.Packet;

import java.util.Collection;

public final class PacketCollections {
	private PacketCollections(){ throw new UnsupportedOperationException(); }

	@Deprecated
	public static PacketCollection createFromPackets(Collection<Packet> packets){
		return new ImmutablePacketCollection(packets, PacketCollectionIdGenerator.Defaults.UNIQUE_GENERATOR);
	}
	public static PacketCollection createFromPackets(Collection<Packet> packets, PacketCollectionIdGenerator idGenerator){
		return new ImmutablePacketCollection(packets, idGenerator);
	}
	public static PacketCollection createFromJson(JsonObject object, JsonPacketGetter packetGetter){
		return new ImmutablePacketCollection(object, packetGetter);
	}
	public interface JsonPacketGetter {
		Packet createFromJson(JsonObject packetObject);
	}
}

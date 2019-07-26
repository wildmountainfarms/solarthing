package me.retrodaredevil.solarthing.packets.collection;

import com.google.gson.JsonObject;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.UnknownPacketTypeException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class JsonPacketGetterMultiplexer implements JsonPacketGetter {
	private final List<JsonPacketGetter> getters;
	
	public JsonPacketGetterMultiplexer(List<? extends JsonPacketGetter> getters) {
		this.getters = Collections.unmodifiableList(new ArrayList<>(getters));
	}
	public JsonPacketGetterMultiplexer(JsonPacketGetter... getters){
		this(Arrays.asList(getters));
	}
	
	@Override
	public Packet createFromJson(JsonObject packetObject) {
		for(JsonPacketGetter getter : getters){
			try {
				return getter.createFromJson(packetObject);
			} catch(UnknownPacketTypeException ex){
			}
		}
		throw new UnknownPacketTypeException("unknown packet type: " + packetObject.getAsJsonPrimitive("packetType").getAsString());
	}
}

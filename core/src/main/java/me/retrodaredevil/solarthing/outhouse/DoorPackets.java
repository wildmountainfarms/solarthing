package me.retrodaredevil.solarthing.outhouse;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public final class DoorPackets {
	private DoorPackets(){ throw new UnsupportedOperationException(); }

	@Deprecated
	public static DoorPacket createFromJson(JsonObject jsonObject){
		return new ImmutableDoorPacket(
			jsonObject.getAsJsonPrimitive("isOpen").getAsBoolean(),
			getLongOrNull(jsonObject, "lastCloseTimeMillis"),
			getLongOrNull(jsonObject, "lastOpenTimeMillis")
		);
	}
	private static Long getLongOrNull(JsonObject object, String name){
		final JsonElement element;
		element = object.get(name);
		if(element == null || element.isJsonNull()){
			return null;
		}
		return element.getAsLong();
	}
}

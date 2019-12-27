package me.retrodaredevil.solarthing.solar.outback.command.packets;

import com.google.gson.JsonObject;
import me.retrodaredevil.solarthing.solar.outback.command.MateCommand;

public final class SuccessMateCommandPackets {
	private SuccessMateCommandPackets(){ throw new UnsupportedOperationException(); }

	@Deprecated
	public static SuccessMateCommandPacket createFromJson(JsonObject jsonObject){
		return new ImmutableSuccessMateCommandPacket(
			MateCommand.valueOf(jsonObject.getAsJsonPrimitive("command").getAsString()),
			jsonObject.getAsJsonPrimitive("source").getAsString()
		);
	}
}

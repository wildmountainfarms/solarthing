package me.retrodaredevil.solarthing.solar.outback.fx.extra;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.retrodaredevil.solarthing.solar.outback.OutbackIdentifier;

import java.util.Collection;
import java.util.HashSet;

import static me.retrodaredevil.util.json.JsonHelper.getOrNull;

public final class DailyFXPackets {
	private DailyFXPackets(){ throw new UnsupportedOperationException(); }

	public static DailyFXPacket createFromJson(JsonObject object){
		// This isn't something we can calculate, so if someone wants to re-serialize it, it may be serialized as null,
		// and when deserializing it again, we want that to be valid // This is why we have treatJsonNullAsUndefined = true
		final Long startDateMillis = getOrNull(object, "startDateMillis", JsonElement::getAsLong, true);
		final float dailyMinBatteryVoltage = object.get("dailyMinBatteryVoltage").getAsFloat();
		final float dailyMaxBatteryVoltage = object.get("dailyMaxBatteryVoltage").getAsFloat();

		final float inverterKWH = object.get("inverterKWH").getAsFloat();
		final float chargerKWH = object.get("chargerKWH").getAsFloat();
		final float buyKWH = object.get("buyKWH").getAsFloat();
		final float sellKWH = object.get("sellKWH").getAsFloat();

		final Collection<Integer> operationalModeValues = new HashSet<>();
		for(JsonElement element : object.get("operationalModeValues").getAsJsonArray()) {
			int operationalModeValue = element.getAsInt();
			operationalModeValues.add(operationalModeValue);
		}
		final int errorModeValue = object.get("errorModeValue").getAsInt();
		final int warningModeValue = object.get("warningModeValue").getAsInt();
		final int miscValue = object.get("miscValue").getAsInt();
		final Collection<Integer> acModeValues = new HashSet<>();
		for(JsonElement element : object.get("acModeValues").getAsJsonArray()){
			int acModeValue = element.getAsInt();
			acModeValues.add(acModeValue);
		}

		final int address = object.get("address").getAsInt();

		return new ImmutableDailyFXPacket(
				startDateMillis,
				dailyMinBatteryVoltage, dailyMaxBatteryVoltage,
				inverterKWH, chargerKWH, buyKWH, sellKWH,
				operationalModeValues,
				errorModeValue,
				warningModeValue,
				miscValue,
				acModeValues,
				new OutbackIdentifier(address)
		);
	}
}

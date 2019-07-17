package me.retrodaredevil.solarthing.solar.renogy.rover;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.Base64;

public final class RoverStatusPackets {
	private RoverStatusPackets(){ throw new UnsupportedOperationException(); }
	
	public static RoverStatusPacket createFromJson(JsonObject object){
		return new ImmutableRoverStatusPacket(
			getInt(object, "maxVoltage"), getInt(object, "ratedChargingCurrent"),
			getInt(object, "ratedDischargingCurrent"), getInt(object, "productType"),
			Base64.getDecoder().decode(object.getAsJsonPrimitive("productModelEncoded").getAsString()),
			getInt(object, "softwareVersion"), getInt(object, "hardwareVersion"),
			getInt(object, "productSerialNumber"), getInt(object, "controllerDeviceAddress"),
			getInt(object, "batteryCapacitySOC"), getFloat(object, "batteryVoltage"),
			getInt(object, "chargingCurrent"),getInt(object, "controllerTemperature"), getInt(object, "batteryTemperature"),
			getFloat(object, "loadVoltage"), getFloat(object, "loadCurrent"),
			getInt(object, "loadPower"), getFloat(object, "inputVoltage"), getFloat(object, "pvCurrent"),
			getInt(object, "chargingPower"),getFloat(object, "dailyMinBatteryVoltage"),getFloat(object, "dailyMaxBatteryVoltage"),
			getFloat(object, "dailyMaxChargingCurrent"), getFloat(object, "dailyMaxDischargingCurrent"),
			getInt(object, "dailyMaxChargingPower"),getInt(object, "dailyMaxDischargingPower"),
			getInt(object, "dailyAH"),getInt(object, "dailyAHDischarging"),
			getFloat(object, "dailyKWH"), getFloat(object, "dailyKWHConsumption"),
			getInt(object, "operatingDaysCount"),
			getInt(object, "batteryOverDischargesCount"),getInt(object, "batteryFullChargesCount"),
			getInt(object, "chargingAmpHoursOfBatteryCount"), getInt(object, "dischargingAmpHoursOfBatteryCount"),
			getFloat(object, "cumulativeKWH"),getFloat(object, "cumulativeKWHConsumption"),
			getInt(object, "streetLightValue"),getInt(object, "chargingState"),
			getInt(object, "errorMode"), getInt(object, "nominalBatteryCapacity"),
			getInt(object, "systemVoltageSetting"),getInt(object, "recognizedVoltage"),
			getInt(object, "batteryType"),
			getInt(object, "overVoltageThresholdRaw"),getInt(object, "chargingVoltageLimitRaw"),
			getInt(object, "equalizingChargingVoltageRaw"), getInt(object, "boostChargingVoltageRaw"),
			getInt(object, "floatingChargingVoltageRaw"),getInt(object, "boostChargingRecoveryVoltageRaw"),
			getInt(object, "overDischargeRecoveryVoltageRaw"),getInt(object, "underVoltageWarningLevelRaw"),
			getInt(object, "overDischargeVoltageRaw"),getInt(object, "dischargingLimitVoltageRaw"),
			getInt(object, "endOfChargeSOC"),getInt(object, "endOfDischargeSOC"),
			getInt(object, "overDischargeTimeDelaySeconds"),
			getInt(object, "equalizingChargingTimeRaw"),getInt(object, "boostChargingTimeRaw"),
			getInt(object, "equalizingChargingIntervalRaw"),getInt(object, "temperatureCompensationFactorRaw"),
			getInt(object, "loadWorkingMode"),
			getInt(object, "lightControlDelayMinutes"),getInt(object, "lightControlVoltage"),
			getInt(object, "ledLoadCurrentSettingRaw"),
			getInt(object, "specialPowerControlE021Raw"),
			getInt(object, "sensingTimeDelayRaw"), getInt(object, "ledLoadCurrentRaw"),
			getInt(object, "specialPowerControlE02DRaw")
		);
	}
	private static JsonPrimitive getPrimitive(JsonObject object, String name){
		JsonElement element = object.get(name);
		if(!element.isJsonPrimitive()){
			throw new RuntimeException(name + " is not a json primitive!");
		}
		JsonPrimitive primitive = element.getAsJsonPrimitive();
		if(!primitive.isNumber()){
			throw new RuntimeException(name + " is not a number!");
		}
		return primitive;
	}
	private static int getInt(JsonObject object, String name){
		JsonPrimitive primitive = getPrimitive(object, name);
		if(primitive.getAsDouble() % 1 != 0){
			throw new RuntimeException(name + " is not an int!");
		}
		return primitive.getAsInt();
	}
	private static float getFloat(JsonObject object, String name){
		return getPrimitive(object, name).getAsInt();
	}
}

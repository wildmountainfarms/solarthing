package me.retrodaredevil.solarthing.solar.renogy.rover;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.Base64;

public final class RoverStatusPackets {
	private RoverStatusPackets(){ throw new UnsupportedOperationException(); }
	
	public static RoverStatusPacket createFromReadTable(RoverReadTable read){
		return new ImmutableRoverStatusPacket(
			read.getMaxVoltageValue(), read.getRatedChargingCurrentValue(),
			read.getRatedDischargingCurrentValue(), read.getProductTypeValue(),
			read.getProductModelValue(),
			read.getSoftwareVersionValue(), read.getHardwareVersionValue(),
			read.getProductSerialNumber(), read.getControllerDeviceAddress(),
			read.getBatteryCapacitySOC(), read.getBatteryVoltage(),
			read.getChargingCurrent(), read.getControllerTemperatureRaw(), read.getBatteryTemperatureRaw(),
			read.getLoadVoltage(), read.getLoadCurrent(),
			read.getLoadPower(), read.getInputVoltage(), read.getPVCurrent(),
			read.getChargingPower(), read.getDailyMinBatteryVoltage(), read.getDailyMaxBatteryVoltage(),
			read.getDailyMaxChargingCurrent(), read.getDailyMaxDischargingCurrent(),
			read.getDailyMaxChargingPower(), read.getDailyMaxDischargingPower(),
			read.getDailyAH(), read.getDailyAHDischarging(),
			read.getDailyKWH(), read.getDailyKWHConsumption(),
			read.getOperatingDaysCount(),
			read.getBatteryOverDischargesCount(), read.getBatteryFullChargesCount(),
			read.getChargingAmpHoursOfBatteryCount(), read.getDischargingAmpHoursOfBatteryCount(),
			read.getCumulativeKWH(), read.getCumulativeKWHConsumption(),
			read.getStreetLightValue(), read.getChargingStateValue(),
			read.getErrorMode(), read.getNominalBatteryCapacity(),
			read.getSystemVoltageSettingValue(), read.getRecognizedVoltageValue(),
			read.getBatteryTypeValue(),
			read.getOverVoltageThresholdRaw(), read.getChargingVoltageLimitRaw(),
			read.getEqualizingChargingVoltageRaw(), read.getBoostChargingVoltageRaw(),
			read.getFloatingChargingVoltageRaw(), read.getBoostChargingRecoveryVoltageRaw(),
			read.getOverDischargeRecoveryVoltageRaw(), read.getUnderVoltageWarningLevelRaw(),
			read.getOverDischargeVoltageRaw(), read.getDischargingLimitVoltageRaw(),
			read.getEndOfChargeSOC(), read.getEndOfDischargeSOC(),
			read.getOverDischargeTimeDelaySeconds(),
			read.getEqualizingChargingTimeRaw(), read.getBoostChargingTimeRaw(),
			read.getEqualizingChargingIntervalRaw(), read.getTemperatureCompensationFactorRaw(),
			read.getOperatingSettingBundle(Rover.OperatingSetting.STAGE_1), read.getOperatingSettingBundle(Rover.OperatingSetting.STAGE_2),
			read.getOperatingSettingBundle(Rover.OperatingSetting.STAGE_3), read.getOperatingSettingBundle(Rover.OperatingSetting.MORNING_ON),
			read.getLoadWorkingModeValue(),
			read.getLightControlDelayMinutes(), read.getLightControlVoltage(),
			read.getLEDLoadCurrentSettingRaw(),
			read.getSpecialPowerControlE021Raw(),
			read.getSensingBundle(Rover.Sensing.SENSING_1), read.getSensingBundle(Rover.Sensing.SENSING_2), read.getSensingBundle(Rover.Sensing.SENSING_3),
			read.getSensingTimeDelayRaw(), read.getLEDLoadCurrentRaw(),
			read.getSpecialPowerControlE02DRaw()
		);
	}
	
	public static RoverStatusPacket createFromJson(JsonObject object){
		return new ImmutableRoverStatusPacket(
			getInt(object, "maxVoltage"), getInt(object, "ratedChargingCurrent"),
			getInt(object, "ratedDischargingCurrent"), getInt(object, "productType"),
			Base64.getDecoder().decode(object.getAsJsonPrimitive("productModelEncoded").getAsString()),
			getInt(object, "softwareVersion"), getInt(object, "hardwareVersion"),
			getInt(object, "productSerialNumber"), getInt(object, "controllerDeviceAddress"),
			getInt(object, "batteryCapacitySOC"), getFloat(object, "batteryVoltage"),
			getInt(object, "chargingCurrent"),getInt(object, "controllerTemperatureRaw"), getInt(object, "batteryTemperatureRaw"),
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
			getOperatingSettingBundle(object, "operatingStage1"), getOperatingSettingBundle(object, "operatingStage2"),
			getOperatingSettingBundle(object, "operatingStage3"), getOperatingSettingBundle(object, "operatingMorningOn"),
			getInt(object, "loadWorkingMode"),
			getInt(object, "lightControlDelayMinutes"),getInt(object, "lightControlVoltage"),
			getInt(object, "ledLoadCurrentSettingRaw"),
			getInt(object, "specialPowerControlE021Raw"),
			getSensingBundle(object, "sensed1"), getSensingBundle(object, "sensed2"), getSensingBundle(object, "sensed3"),
			getInt(object, "sensingTimeDelayRaw"), getInt(object, "ledLoadCurrentRaw"),
			getInt(object, "specialPowerControlE02DRaw")
		);
	}
	private static Rover.OperatingSettingBundle getOperatingSettingBundle(JsonObject object, String name){
		JsonElement element = object.get(name);
		if(!element.isJsonObject()){
			throw new RuntimeException(name + " is not a JsonObject!");
		}
		JsonObject inner = element.getAsJsonObject();
		return new Rover.OperatingSettingBundle(getInt(inner, "durationHours"), getInt(inner, "operatingPowerPercentage"));
	}
	private static Rover.SensingBundle getSensingBundle(JsonObject object, String name){
		JsonElement element = object.get(name);
		if(!element.isJsonObject()){
			throw new RuntimeException(name + " is not a JsonObject!");
		}
		JsonObject inner = element.getAsJsonObject();
		return new Rover.SensingBundle(getInt(inner, "workingHoursRaw"), getInt(inner, "powerWithPeopleSensedRaw"), getInt(inner, "powerWithNoPeopleSensedRaw"));
	}
	private static JsonPrimitive getPrimitive(JsonObject object, String name){
		JsonElement element = object.get(name);
		if(element == null){
			throw new NullPointerException(name + " is null! (undefined in JSON)");
		}
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

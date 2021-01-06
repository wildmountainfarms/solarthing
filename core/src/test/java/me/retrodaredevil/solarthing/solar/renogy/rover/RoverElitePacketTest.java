package me.retrodaredevil.solarthing.solar.renogy.rover;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

public class RoverElitePacketTest {
	@Test
	void test() throws JsonProcessingException {
		String json = "{\n" +
				"  \n" +
				"\"packetType\" : \"RENOGY_ROVER_STATUS\",\n" +
				"  \"maxVoltage\" : 24,\n" +
				"  \"ratedChargingCurrent\" : 20,\n" +
				"  \"ratedDischargingCurrent\" : 20,\n" +
				"  \"productType\" : 0,\n" +
				"  \"productModelEncoded\" : \"UkNDMjBSVlJFLUcxICAgIA==\",\n" +
				"  \"softwareVersion\" : 0,\n" +
				"  \"hardwareVersionString\" : \"V00.00.00\",\n" +
				"  \"productSerialNumber\" : 0,\n" +
				"  \"controllerDeviceAddress\" : 1,\n" +
				"  \"batteryCapacitySOC\" : 100,\n" +
				"  \"batteryVoltage\" : 13.8,\n" +
				"  \"chargingCurrent\" : 0.3,\n" +
				"  \"controllerTemperatureRaw\" : 26,\n" +
				"  \"batteryTemperatureRaw\" : 20,\n" +
				"  \"loadVoltage\" : 0.0,\n" +
				"  \"loadCurrent\" : 0.0,\n" +
				"  \"loadPower\" : 0,\n" +
				"  \"inputVoltage\" : 18.5,\n" +
				"  \"pvCurrent\" : 0.21,\n" +
				"  \"chargingPower\" : 4,\n" +
				"  \"dailyMinBatteryVoltage\" : 12.5,\n" +
				"  \"dailyMaxBatteryVoltage\" : 14.3,\n" +
				"  \"dailyMaxChargingCurrent\" : 3.6,\n" +
				"  \"dailyMaxDischargingCurrent\" : 0.0,\n" +
				"  \"dailyMaxChargingPower\" : 40,\n" +
				"  \"dailyMaxDischargingPower\" : 0,\n" +
				"  \"dailyAH\" : 10,\n" +
				"  \"dailyAHDischarging\" : 0,\n" +
				"  \"dailyKWH\" : 0.131,\n" +
				"  \"dailyKWHConsumption\" : 0.0,\n" +
				"  \"operatingDaysCount\" : 7,\n" +
				"  \"batteryOverDischargesCount\" : 0,\n" +
				"  \"batteryFullChargesCount\" : 0,\n" +
				"  \"chargingAmpHoursOfBatteryCount\" : 18,\n" +
				"  \"dischargingAmpHoursOfBatteryCount\" : 0,\n" +
				"  \"cumulativeKWH\" : 0.219,\n" +
				"  \"cumulativeKWHConsumption\" : 0.0,\n" +
				"  \"streetLightValue\" : 0,\n" +
				"  \"chargingState\" : 5,\n" +
				"  \"errorMode\" : 0,\n" +
				"  \"nominalBatteryCapacity\" : 200,\n" +
				"  \"systemVoltageSetting\" : 255,\n" +
				"  \"recognizedVoltage\" : 12,\n" +
				"  \"batteryType\" : 3,\n" +
				"  \"overVoltageThresholdRaw\" : 160,\n" +
				"  \"chargingVoltageLimitRaw\" : 155,\n" +
				"  \"equalizingChargingVoltageRaw\" : 152,\n" +
				"  \"boostChargingVoltageRaw\" : 142,\n" +
				"  \"floatingChargingVoltageRaw\" : 138,\n" +
				"  \"boostChargingRecoveryVoltageRaw\" : 132,\n" +
				"  \"overDischargeRecoveryVoltageRaw\" : 126,\n" +
				"  \"underVoltageWarningLevelRaw\" : 120,\n" +
				"  \"overDischargeVoltageRaw\" : 111,\n" +
				"  \"dischargingLimitVoltageRaw\" : 106,\n" +
				"  \"endOfChargeSOC\" : 100,\n" +
				"  \"endOfDischargeSOC\" : 50,\n" +
				"  \"overDischargeTimeDelaySeconds\" : 5,\n" +
				"  \"equalizingChargingTimeRaw\" : 0,\n" +
				"  \"boostChargingTimeRaw\" : 120,\n" +
				"  \"equalizingChargingIntervalRaw\" : 0,\n" +
				"  \"temperatureCompensationFactorRaw\" : 3,\n" +
				"  \"operatingStage1\" : {\n" +
				"    \"durationHours\" : 0,\n" +
				"    \"operatingPowerPercentage\" : 0\n" +
				"  },\n" +
				"  \"operatingStage2\" : {\n" +
				"    \"durationHours\" : 0,\n" +
				"    \"operatingPowerPercentage\" : 0\n" +
				"  },\n" +
				"  \"operatingStage3\" : {\n" +
				"    \"durationHours\" : 0,\n" +
				"    \"operatingPowerPercentage\" : 0\n" +
				"  },\n" +
				"  \"operatingMorningOn\" : {\n" +
				"    \"durationHours\" : 0,\n" +
				"    \"operatingPowerPercentage\" : 0\n" +
				"  },\n" +
				"  \"loadWorkingMode\" : 0,\n" +
				"  \"lightControlDelayMinutes\" : 0,\n" +
				"  \"lightControlVoltage\" : 0,\n" +
				"  \"ledLoadCurrentSettingRaw\" : 0,\n" +
				"  \"specialPowerControlE021Raw\" : 0,\n" +
				"  \"sensed1\" : null,\n" +
				"  \"sensed2\" : null,\n" +
				"  \"sensed3\" : null,\n" +
				"  \"sensingTimeDelayRaw\" : null,\n" +
				"  \"ledLoadCurrentRaw\" : null,\n" +
				"  \"specialPowerControlE02DRaw\" : null,\n" +
				"  \"productModelString\" : \"RCC20RVRE-G1\",\n" +
				"  \"softwareVersionString\" : \"V00.00.00\",\n" +
				"  \"hardwareVersion\" : 0,\n" +
				"  \"streetLightBrightness\" : 0,\n" +
				"  \"streetLightOn\" : false,\n" +
				"  \"chargingStateName\" : \"Float\",\n" +
				"  \"errors\" : \"\",\n" +
				"  \"batteryTypeName\" : \"gel\",\n" +
				"  \"loadWorkingModeName\" : \"LIGHT_CONTROL\"\n" +
				"}";
		ObjectMapper mapper = JacksonUtil.defaultMapper();
		RoverStatusPacket roverStatusPacket = mapper.readValue(json, RoverStatusPacket.class);
		assertNull(roverStatusPacket.getSensed1());
		assertNull(roverStatusPacket.getSensed2());
		assertNull(roverStatusPacket.getSensed3());
		assertNull(roverStatusPacket.getSensingTimeDelayRaw());
		assertNull(roverStatusPacket.getLEDLoadCurrentRaw());
		assertNull(roverStatusPacket.getSpecialPowerControlE02DRaw());
		assertFalse(roverStatusPacket.supportsMesLoad());
	}
}

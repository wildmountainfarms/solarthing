package me.retrodaredevil.solarthing.solar.renogy.rover;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

@NullMarked
public class RoverElitePacketTest {
	@Test
	void test() throws JsonProcessingException {
		String json = """
				{
					"packetType" : "RENOGY_ROVER_STATUS",
					"maxVoltage" : 24,
					"ratedChargingCurrent" : 20,
					"ratedDischargingCurrent" : 20,
					"productType" : 0,
					"productModelEncoded" : "UkNDMjBSVlJFLUcxICAgIA==",
					"softwareVersion" : 0,
					"hardwareVersionString" : "V00.00.00",
					"productSerialNumber" : 0,
					"controllerDeviceAddress" : 1,
					"batteryCapacitySOC" : 100,
					"batteryVoltage" : 13.8,
					"chargingCurrent" : 0.3,
					"controllerTemperatureRaw" : 26,
					"batteryTemperatureRaw" : 20,
					"loadVoltage" : 0.0,
					"loadCurrent" : 0.0,
					"loadPower" : 0,
					"inputVoltage" : 18.5,
					"pvCurrent" : 0.21,
					"chargingPower" : 4,
					"dailyMinBatteryVoltage" : 12.5,
					"dailyMaxBatteryVoltage" : 14.3,
					"dailyMaxChargingCurrent" : 3.6,
					"dailyMaxDischargingCurrent" : 0.0,
					"dailyMaxChargingPower" : 40,
					"dailyMaxDischargingPower" : 0,
					"dailyAH" : 10,
					"dailyAHDischarging" : 0,
					"dailyKWH" : 0.131,
					"dailyKWHConsumption" : 0.0,
					"operatingDaysCount" : 7,
					"batteryOverDischargesCount" : 0,
					"batteryFullChargesCount" : 0,
					"chargingAmpHoursOfBatteryCount" : 18,
					"dischargingAmpHoursOfBatteryCount" : 0,
					"cumulativeKWH" : 0.219,
					"cumulativeKWHConsumption" : 0.0,
					"streetLightValue" : 0,
					"chargingState" : 5,
					"errorMode" : 0,
					"nominalBatteryCapacity" : 200,
					"systemVoltageSetting" : 255,
					"recognizedVoltage" : 12,
					"batteryType" : 3,
					"overVoltageThresholdRaw" : 160,
					"chargingVoltageLimitRaw" : 155,
					"equalizingChargingVoltageRaw" : 152,
					"boostChargingVoltageRaw" : 142,
					"floatingChargingVoltageRaw" : 138,
					"boostChargingRecoveryVoltageRaw" : 132,
					"overDischargeRecoveryVoltageRaw" : 126,
					"underVoltageWarningLevelRaw" : 120,
					"overDischargeVoltageRaw" : 111,
					"dischargingLimitVoltageRaw" : 106,
					"endOfChargeSOC" : 100,
					"endOfDischargeSOC" : 50,
					"overDischargeTimeDelaySeconds" : 5,
					"equalizingChargingTimeRaw" : 0,
					"boostChargingTimeRaw" : 120,
					"equalizingChargingIntervalRaw" : 0,
					"temperatureCompensationFactorRaw" : 3,
					"operatingStage1" : {
						"durationHours" : 0,
						"operatingPowerPercentage" : 0
					},
					"operatingStage2" : {
						"durationHours" : 0,
						"operatingPowerPercentage" : 0
					},
					"operatingStage3" : {
						"durationHours" : 0,
						"operatingPowerPercentage" : 0
					},
					"operatingMorningOn" : {
						"durationHours" : 0,
						"operatingPowerPercentage" : 0
					},
					"loadWorkingMode" : 0,
					"lightControlDelayMinutes" : 0,
					"lightControlVoltage" : 0,
					"ledLoadCurrentSettingRaw" : 0,
					"specialPowerControlE021Raw" : 0,
					"sensed1" : null,
					"sensed2" : null,
					"sensed3" : null,
					"sensingTimeDelayRaw" : null,
					"ledLoadCurrentRaw" : null,
					"specialPowerControlE02DRaw" : null,
					"productModelString" : "RCC20RVRE-G1",
					"softwareVersionString" : "V00.00.00",
					"hardwareVersion" : 0,
					"streetLightBrightness" : 0,
					"streetLightOn" : false,
					"chargingStateName" : "Float",
					"errors" : "",
					"batteryTypeName" : "gel",
					"loadWorkingModeName" : "LIGHT_CONTROL"
				}""";
		ObjectMapper mapper = JacksonUtil.defaultMapper();
		RoverStatusPacket roverStatusPacket = mapper.readValue(json, RoverStatusPacket.class);
		assertNull(roverStatusPacket.getSensed1());
		assertNull(roverStatusPacket.getSensed2());
		assertNull(roverStatusPacket.getSensed3());
		assertNull(roverStatusPacket.getSensingTimeDelayRaw());
		assertNull(roverStatusPacket.getLEDLoadCurrentRaw());
		assertNull(roverStatusPacket.getSpecialPowerControlE02DRaw());
		assertNull(roverStatusPacket.getPacketVersion());
		assertFalse(roverStatusPacket.supportsMesLoad());
	}
}

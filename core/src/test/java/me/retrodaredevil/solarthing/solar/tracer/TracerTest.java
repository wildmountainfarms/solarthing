package me.retrodaredevil.solarthing.solar.tracer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.solarthing.PacketTestUtil;
import me.retrodaredevil.solarthing.solar.SolarStatusPacket;
import me.retrodaredevil.solarthing.solar.event.SolarEventPacket;
import me.retrodaredevil.solarthing.solar.tracer.event.ImmutableTracerChargingEquipmentStatusChangePacket;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TracerTest {
	private static final File DIRECTORY_TRACER = new File(PacketTestUtil.SOLARTHING_ROOT, "testing/packets/tracer");

	@Test
	void test() throws JsonProcessingException {
		TracerStatusPacket packet = new ImmutableTracerStatusPacket(
				TracerStatusPacket.Version.LATEST, null,
				100, 20, 200, 24, 20, 200,
				1, 20, 0.0f, 0.0f, 0.0f,
				12.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 25.0f, 26.0f, 26.0f,
				50, 0.0f, 12, 0, 2 << 2, 0.0f,
				0.0f, 0.0f, 11.9f, 0.0f, 0.0f, 0.0f, 0.0f,
				0.0f, 0.0f, 0.0f, 0.0f,
				0.0f, 0.0f, 25.0f, 25.0f, 0, 300, 3,
				15.0f, 14.85f, 14.5f, 14.0f, 13.8f, 13.2f, 12.8f,
				11.81f, 11.82f, 11.83f, 11.2f, 11.84f, 14297963433015L,
				30, 65.0f, -40.0f, 85.0f, 75.0f, 85.0f, 75.0f,
				0, 3.0f, 10, 6.0f, 10, 0, 256, 256,
				81604378624L, 25769803776L, 81604378624L, 25769803776L,
				524, 0, 0, false, 120, 120, 30, 100,
				0, true, false, false, false, true
		);
		testWithPacket(packet);
	}
	@Test
	void testWithRealJson() throws JsonProcessingException {
		String json = "{ \"packetType\" : \"TRACER_STATUS\", \"ratedInputVoltage\" : 100, \"ratedInputCurrent\" : 20, \"ratedInputPower\" : 520, \"ratedOutputVoltage\" : 24, \"ratedOutputCurrent\" : 20, \"ratedOutputPower\" : 520, \"chargingTypeValue\" : 2, \"ratedLoadOutputCurrent\" : 20, \"inputVoltage\" : 0.5, \"pvCurrent\" : 0.0, \"pvWattage\" : 0.0, \"batteryVoltage\" : 12.34, \"chargingCurrent\" : 0.0, \"chargingPower\" : 0.0, \"loadVoltage\" : 0.0, \"loadCurrent\" : 0.0, \"loadPower\" : 0.0, \"batteryTemperatureCelsius\" : 18.11, \"insideControllerTemperatureCelsius\" : 19.19, \"powerComponentTemperatureCelsius\" : 19.19, \"batterySOC\" : 45, \"remoteBatteryTemperatureCelsius\" : 0.0, \"realBatteryRatedVoltageValue\" : 12, \"batteryStatusValue\" : 0, \"chargingEquipmentStatusValue\" : 0, \"dailyMaxInputVoltage\" : 21.03, \"dailyMinInputVoltage\" : 0.0, \"dailyMaxBatteryVoltage\" : 14.44, \"dailyMinBatteryVoltage\" : 12.08, \"dailyKWHConsumption\" : 0.0, \"monthlyKWHConsumption\" : 0.0, \"yearlyKWHConsumption\" : 0.0, \"cumulativeKWHConsumption\" : 0.0, \"dailyKWH\" : 0.06, \"monthlyKWH\" : 0.06, \"yearlyKWH\" : 0.06, \"cumulativeKWH\" : 0.06, \"carbonDioxideReductionTons\" : 0.0, \"netBatteryCurrent\" : 0.0, \"batteryTemperatureCelsius331D\" : 18.1, \"ambientTemperatureCelsius\" : 18.1, \"batteryTypeValue\" : 1, \"batteryCapacityAmpHours\" : 200, \"temperatureCompensationCoefficient\" : 3, \"highVoltageDisconnect\" : 16.0, \"chargingLimitVoltage\" : 15.0, \"overVoltageReconnect\" : 15.0, \"equalizationVoltage\" : 14.6, \"boostVoltage\" : 14.4, \"floatVoltage\" : 13.8, \"boostReconnectVoltage\" : 13.2, \"lowVoltageReconnect\" : 12.6, \"underVoltageRecover\" : 12.2, \"underVoltageWarning\" : 12.0, \"lowVoltageDisconnect\" : 11.1, \"dischargingLimitVoltage\" : 10.6, \"secondMinuteHourDayMonthYearRaw\" : 14297963963419, \"equalizationChargingCycleDays\" : 30, \"batteryTemperatureWarningUpperLimit\" : 65.0, \"batteryTemperatureWarningLowerLimit\" : -40.0, \"insideControllerTemperatureWarningUpperLimit\" : 85.0, \"insideControllerTemperatureWarningUpperLimitRecover\" : 75.0, \"powerComponentTemperatureWarningUpperLimit\" : 85.0, \"powerComponentTemperatureWarningUpperLimitRecover\" : 75.0, \"lineImpedance\" : 0.0, \"nightPVVoltageThreshold\" : 5.0, \"lightSignalStartupDelayTime\" : 10, \"dayPVVoltageThreshold\" : 6.0, \"lightSignalTurnOffDelayTime\" : 10, \"loadControlModeValue\" : 0, \"workingTimeLength1Raw\" : 256, \"workingTimeLength2Raw\" : 256, \"turnOnTiming1Raw\" : 81604378624, \"turnOffTiming1Raw\" : 25769803776, \"turnOnTiming2Raw\" : 81604378624, \"turnOffTiming2Raw\" : 25769803776, \"lengthOfNightRaw\" : 524, \"batteryRatedVoltageCode\" : 0, \"loadTimingControlSelectionValue\" : 0, \"isLoadOnByDefaultInManualMode\" : true, \"equalizeDurationMinutes\" : 120, \"boostDurationMinutes\" : 120, \"dischargingPercentage\" : 30, \"chargingPercentage\" : 100, \"batteryManagementModeValue\" : 0, \"isManualLoadControlOn\" : false, \"isLoadTestModeEnabled\" : false, \"isLoadForcedOn\" : false, \"isInsideControllerOverTemperature\" : false, \"isNight\" : true }";
		ObjectMapper mapper = JacksonUtil.defaultMapper();
		TracerStatusPacket parsed = mapper.readValue(json, TracerStatusPacket.class);
	}

	/**
	 * This tests to make sure serialization and deserialization works as expected
	 */
	private void testWithPacket(TracerStatusPacket packet) throws JsonProcessingException {
		ObjectMapper mapper = JacksonUtil.defaultMapper();
		String json = mapper.writeValueAsString(packet);
		System.out.println(json);
		TracerStatusPacket parsed = mapper.readValue(json, TracerStatusPacket.class);

		String json2 = mapper.writeValueAsString(parsed);
		assertEquals(json, json2, "JSON deserialization likely is incorrect");

		TracerStatusPacket readPacket = TracerStatusPackets.createFromReadTable(parsed.getIdentifier().getNumber(), parsed);
		String json3 = mapper.writeValueAsString(readPacket);
		assertEquals(json, json3, "TracerStatusPackets.createFromReadTable is not correct!");
	}

	@Test
	void testTracerEvents() throws JsonProcessingException {
		// 1 is off
		// 15 is something that is not off idk what tho
		PacketTestUtil.testJson(new ImmutableTracerChargingEquipmentStatusChangePacket(TracerIdentifier.getFromNumber(0), 1, 15), SolarEventPacket.class);
		PacketTestUtil.testJson(new ImmutableTracerChargingEquipmentStatusChangePacket(TracerIdentifier.getFromNumber(1), 15, null), SolarEventPacket.class);
	}

	@Test
	void testTracerExisting() throws IOException {
		assertTrue(DIRECTORY_TRACER.isDirectory());

		ObjectMapper mapper = JacksonUtil.defaultMapper();

		for (File file : requireNonNull(DIRECTORY_TRACER.listFiles())) {
			SolarStatusPacket packet = mapper.readValue(file, SolarStatusPacket.class);
			assertTrue(packet instanceof TracerStatusPacket, "Got packet: " + packet);
		}
	}
}

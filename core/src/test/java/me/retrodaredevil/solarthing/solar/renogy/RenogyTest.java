package me.retrodaredevil.solarthing.solar.renogy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.solarthing.PacketTestUtil;
import me.retrodaredevil.solarthing.solar.SolarStatusPacket;
import me.retrodaredevil.solarthing.solar.event.SolarEventPacket;
import me.retrodaredevil.solarthing.solar.renogy.rover.*;
import me.retrodaredevil.solarthing.solar.renogy.rover.event.ImmutableRoverChargingStateChangePacket;
import me.retrodaredevil.solarthing.solar.renogy.rover.event.ImmutableRoverErrorModeChangePacket;
import me.retrodaredevil.solarthing.solar.renogy.rover.special.MutableSpecialPowerControl_E021;
import me.retrodaredevil.solarthing.solar.renogy.rover.special.MutableSpecialPowerControl_E02D;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.*;

final class RenogyTest {
	private static final File DIRECTORY_ROVER = new File(PacketTestUtil.SOLARTHING_ROOT, "testing/packets/rover");

	@Test
	void temperatureConvertTest(){
		assertEquals(2, RoverReadTable.convertRawTemperature(2));
		assertEquals(-2, RoverReadTable.convertRawTemperature(2 | (1 << 7)));
		assertEquals(-30, RoverReadTable.convertRawTemperature(30 | (1 << 7)));
	}
	@Test
	void testStreetLight(){
		assertTrue(StreetLight.OFF.isActive(~StreetLight.IGNORED_BITS & 0b01111111));
		assertTrue(StreetLight.OFF.isActive(~StreetLight.IGNORED_BITS & 0b01111011));
		assertTrue(StreetLight.OFF.isActive(~StreetLight.IGNORED_BITS & 0b00111011));
		assertTrue(StreetLight.OFF.isActive(~StreetLight.IGNORED_BITS & 0b00111010));

		assertTrue(StreetLight.ON.isActive(~StreetLight.IGNORED_BITS & 0b11111111));
		assertTrue(StreetLight.ON.isActive(~StreetLight.IGNORED_BITS & 0b11111011));
		assertTrue(StreetLight.ON.isActive(~StreetLight.IGNORED_BITS & 0b10111011));
		assertTrue(StreetLight.ON.isActive(~StreetLight.IGNORED_BITS & 0b10111010));

		assertEquals(100, StreetLight.getBrightnessValue(100));
		assertEquals(100, StreetLight.getBrightnessValue(100 | (1 << 7)));
		assertEquals(49, StreetLight.getBrightnessValue(49));
		assertEquals(49, StreetLight.getBrightnessValue(49 | (1 << 7)));
	}
	@Test
	void testSpecialPower_E021(){
		MutableSpecialPowerControl_E021 power = new MutableSpecialPowerControl_E021();

		power.setChargingModeControlledByVoltage(true);
		assertTrue(power.isChargingModeControlledByVoltage());
		power.setChargingModeControlledByVoltage(false);
		assertFalse(power.isChargingModeControlledByVoltage());

		power.setSpecialPowerControlEnabled(true);
		assertTrue(power.isSpecialPowerControlEnabled());
		power.setSpecialPowerControlEnabled(false);
		assertFalse(power.isSpecialPowerControlEnabled());

		power.setEachNightOnEnabled(true);
		assertTrue(power.isEachNightOnEnabled());
		power.setEachNightOnEnabled(false);
		assertFalse(power.isEachNightOnEnabled());

		power.setNoChargingBelow0CEnabled(true);
		assertTrue(power.isNoChargingBelow0CEnabled());
		power.setNoChargingBelow0CEnabled(false);
		assertFalse(power.isNoChargingBelow0CEnabled());

		power.setChargingMethod(ChargingMethod.DIRECT);
		assertEquals(ChargingMethod.DIRECT, power.getChargingMethod().getChargingMethod());
		power.setChargingMethod(ChargingMethod.PWM);
		assertEquals(ChargingMethod.PWM, power.getChargingMethod().getChargingMethod());

		assertFalse(power.isChargingModeControlledByVoltage());
		assertFalse(power.isSpecialPowerControlEnabled());
		assertFalse(power.isEachNightOnEnabled());
		assertFalse(power.isNoChargingBelow0CEnabled());
		assertEquals(ChargingMethod.PWM, power.getChargingMethod().getChargingMethod());
	}
	@Test
	void testSpecialPower_E02D(){
		MutableSpecialPowerControl_E02D power = new MutableSpecialPowerControl_E02D();

		power.setIntelligentPowerEnabled(true);
		assertTrue(power.isIntelligentPowerEnabled());
		power.setIntelligentPowerEnabled(false);
		assertFalse(power.isIntelligentPowerEnabled());

		power.setEachNightOnEnabled(true);
		assertTrue(power.isEachNightOnEnabled());
		power.setEachNightOnEnabled(false);
		assertFalse(power.isEachNightOnEnabled());

		power.setIsLithiumBattery(true);
		assertTrue(power.isLithiumBattery());
		power.setIsLithiumBattery(false);
		assertFalse(power.isLithiumBattery());

		power.setChargingMethod(ChargingMethod.DIRECT);
		assertEquals(ChargingMethod.DIRECT, power.getChargingMethod().getChargingMethod());
		power.setChargingMethod(ChargingMethod.PWM);
		assertEquals(ChargingMethod.PWM, power.getChargingMethod().getChargingMethod());

		power.setNoChargingBelow0CEnabled(true);
		assertTrue(power.isNoChargingBelow0CEnabled());
		power.setNoChargingBelow0CEnabled(false);
		assertFalse(power.isNoChargingBelow0CEnabled());

		power.setIs24VSystem(true);
		assertTrue(power.is24VSystem());
		power.setIs24VSystem(false);
		assertFalse(power.is24VSystem());

		assertFalse(power.isIntelligentPowerEnabled());
		assertFalse(power.isEachNightOnEnabled());
		assertFalse(power.isLithiumBattery());
		assertEquals(ChargingMethod.PWM, power.getChargingMethod().getChargingMethod());
		assertFalse(power.isNoChargingBelow0CEnabled());
		assertFalse(power.is24VSystem());
	}
	@Test
	void testPacket() throws JsonProcessingException {
		Rover.OperatingSettingBundle operating = new Rover.OperatingSettingBundle(0, 0);
		Rover.SensingBundle sensing = new Rover.SensingBundle(0, 0, 0);
		RoverStatusPacket packet = new ImmutableRoverStatusPacket(
				null, null, 12,30,0,0,
				"     HI         ".getBytes(StandardCharsets.US_ASCII),
				0,0,
				0,0,0,26.2f,0,
				0,0,0,0,0,0,0,
				0,0,0,0,0,0,
				0,0,0,0,0,
				0,0,0,0,0,0,
				0,0,0,0,0,0,0,
				RoverBatteryType.OPEN.getValueCode(),0,0,0,0,0,
				0,0,0,0,0,
				0,0,0,0,0,0,
				0, operating, operating, operating, operating, 0,0,0,0,
				0, sensing, sensing, sensing, 0,0,0
		);
		ObjectMapper mapper = JacksonUtil.defaultMapper();
		String json = mapper.writeValueAsString(packet);
		System.out.println(json);
		RoverStatusPacket parsed = mapper.readValue(json, RoverStatusPacket.class);

		String json2 = mapper.writeValueAsString(parsed);
		assertEquals(json, json2);

		assertFalse(parsed.isNewDay(parsed)); // because they're the same, just do a quick check to make sure this method returns false. Maybe we'll add another test for it later...
	}
	@Test
	void productModelTest(){
		assertTrue(ProductModelUtil.isPositiveGround("RNG-CTRL-RVRPG40"));
		assertTrue(ProductModelUtil.isRover("RNG-CTRL-RVRPG40"));
		assertFalse(ProductModelUtil.isWanderer("RNG-CTRL-RVRPG40"));
	}

	@Test
	void testEventPackets() throws JsonProcessingException {
		PacketTestUtil.testJson(new ImmutableRoverChargingStateChangePacket(RoverIdentifier.getFromNumber(0), ChargingState.MPPT.getValueCode(), null), SolarEventPacket.class);
		PacketTestUtil.testJson(new ImmutableRoverChargingStateChangePacket(RoverIdentifier.getFromNumber(0), ChargingState.BOOST.getValueCode(), ChargingState.MPPT.getValueCode()), SolarEventPacket.class);

		PacketTestUtil.testJson(new ImmutableRoverErrorModeChangePacket(
				RoverIdentifier.getFromNumber(0),
				RoverErrorMode.BATTERY_OVER_DISCHARGE.getMaskValue() | RoverErrorMode.AMBIENT_TEMP_HIGH.getMaskValue(),
				null
		), SolarEventPacket.class);
		PacketTestUtil.testJson(new ImmutableRoverErrorModeChangePacket(
				RoverIdentifier.getFromNumber(0),
				0,
				RoverErrorMode.BATTERY_OVER_DISCHARGE.getMaskValue() | RoverErrorMode.AMBIENT_TEMP_HIGH.getMaskValue()
		), SolarEventPacket.class);
	}


	@Test
	void testRoverExisting() throws IOException {
		assertTrue(DIRECTORY_ROVER.isDirectory());

		ObjectMapper mapper = JacksonUtil.defaultMapper();

		for (File file : requireNonNull(DIRECTORY_ROVER.listFiles())) {
			SolarStatusPacket packet = mapper.readValue(file, SolarStatusPacket.class);
			assertTrue(packet instanceof RoverStatusPacket, "Got packet: " + packet);
		}
	}

}

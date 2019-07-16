package me.retrodaredevil.solarthing.solar.renogy;

import me.retrodaredevil.solarthing.solar.renogy.rover.StreetLight;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class RenogyTest {
	@Test
	void testStreetLight(){
		assertThrows(IllegalArgumentException.class, () -> StreetLight.OFF.isActive(256));
		
		assertTrue(StreetLight.OFF.isActive(0b01111111));
		assertTrue(StreetLight.OFF.isActive(0b01111011));
		assertTrue(StreetLight.OFF.isActive(0b00111011));
		assertTrue(StreetLight.OFF.isActive(0b00111010));
		
		assertTrue(StreetLight.ON.isActive(0b11111111));
		assertTrue(StreetLight.ON.isActive(0b11111011));
		assertTrue(StreetLight.ON.isActive(0b10111011));
		assertTrue(StreetLight.ON.isActive(0b10111010));
		
		assertEquals(100, StreetLight.getBrightnessValue(100));
		assertEquals(100, StreetLight.getBrightnessValue(100 | (1 << 7)));
		assertEquals(49, StreetLight.getBrightnessValue(49));
		assertEquals(49, StreetLight.getBrightnessValue(49 | (1 << 7)));
	}
}

package me.retrodaredevil.solarthing.util.integration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IntegralAccumulatorTest {
	@Test
	void testTrapezoidal(){
		MutableIntegral integral = new TrapezoidalRuleAccumulator();
		assertEquals(0.0, integral.getIntegral());

		integral.add(1, 100);
		assertEquals(0.0, integral.getIntegral());
		integral.add(2, 100);
		assertEquals(100, integral.getIntegral());
		integral.add(3, 100);
		assertEquals(200, integral.getIntegral());

		integral.add(4, 200);
		assertEquals(350, integral.getIntegral());
	}
}

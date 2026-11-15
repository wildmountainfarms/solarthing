package me.retrodaredevil.solarthing.util.integration;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@NullMarked
class IntegralAccumulatorTest {
	@Test
	void testTrapezoidal(){
		MutableIntegral integral = new TrapezoidalRuleAccumulator();
		assertEquals(0.0, integral.getIntegral());

		integral.add(Instant.EPOCH.plusSeconds(1 * 3600L), 100);
		assertEquals(0.0, integral.getIntegral());
		integral.add(Instant.EPOCH.plusSeconds(2 * 3600L), 100);
		assertEquals(100, integral.getIntegral());
		integral.add(Instant.EPOCH.plusSeconds(3 * 3600L), 100);
		assertEquals(200, integral.getIntegral());

		integral.add(Instant.EPOCH.plusSeconds(4 * 3600L), 200);
		assertEquals(350, integral.getIntegral());
	}

	@Test
	void testShortIntervalsAtModernTimestamp(){
		Instant start = Instant.parse("2026-11-09T12:00:00Z");
		for (MutableIntegral integral : new MutableIntegral[]{
			new LeftRiemannSumAccumulator(), new RightRiemannSumAccumulator(), new TrapezoidalRuleAccumulator()
		}) {
			integral.add(start, 3600);
			integral.add(start.plusNanos(1), 3600);
			assertEquals(1e-9, integral.getIntegral(), 1e-24);
		}
	}

	@Test
	void testBackwardTimestampDoesNotChangePreviousReading(){
		MutableIntegral integral = new TrapezoidalRuleAccumulator();
		integral.add(Instant.EPOCH, 100);
		assertThrows(IllegalArgumentException.class, () -> integral.add(Instant.EPOCH.minusSeconds(1), 500));
		integral.add(Instant.EPOCH.plusSeconds(3600), 100);
		assertEquals(100, integral.getIntegral());
	}

	@Test
	void testEqualTimestampUpdatesReading(){
		MutableIntegral integral = new LeftRiemannSumAccumulator();
		integral.add(Instant.EPOCH, 100);
		integral.add(Instant.EPOCH, 200);
		assertEquals(0, integral.getIntegral());
		integral.add(Instant.EPOCH.plusSeconds(3600), 300);
		assertEquals(200, integral.getIntegral());
	}

	@Test
	void testResetPreservesOrClearsPreviousReading(){
		MutableIntegral integral = new LeftRiemannSumAccumulator();
		integral.add(Instant.EPOCH, 100);
		integral.add(Instant.EPOCH.plusSeconds(3600), 200);
		integral.reset();
		integral.add(Instant.EPOCH.plusSeconds(7200), 300);
		assertEquals(200, integral.getIntegral());

		integral.reset(true);
		integral.add(Instant.EPOCH, 400);
		assertEquals(0, integral.getIntegral());
		integral.add(Instant.EPOCH.plusSeconds(3600), 500);
		assertEquals(400, integral.getIntegral());
	}
}

package me.retrodaredevil.solarthing.solar.outback.fx.charge;

import me.retrodaredevil.solarthing.solar.outback.fx.OperationalMode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class FXChargingStateHandlerTest {

	@Test
	void test(){
		FXChargingSettings settings = new FXChargingSettings(
				23.8f,
				28.8f, 60 * 60 * 1000,
				26.8f, 60 * 60 * 1000 * 2, 25.0f,
				29.2f, 60 * 60 * 1000 * 3
		);
		FXChargingStateHandler handler = new FXChargingStateHandler(settings);
		handler.update(1000, OperationalMode.INV_OFF, 23.0f);
		assertNull(handler.getMode());
		handler.update(1000, OperationalMode.CHARGE, 28.6f);
		assertEquals(FXChargingMode.BULK_TO_ABSORB, handler.getMode());

		handler.update(1000, OperationalMode.CHARGE, 28.8f);
		assertEquals(FXChargingMode.ABSORB, handler.getMode());
		handler.update(60 * 60 * 1000 - 1001, OperationalMode.CHARGE, 28.8f); // 1 millisecond before timer expires
		assertEquals(FXChargingMode.ABSORB, handler.getMode());
		assertEquals(1, handler.getRemainingAbsorbTimeMillis());
		handler.update(1, OperationalMode.SILENT, 28.8f);
		assertEquals(FXChargingMode.SILENT, handler.getMode());

		handler.update(1000, OperationalMode.SILENT, 28.8f);
		handler.update(1000, OperationalMode.SILENT, 28.8f);
		assertEquals(FXChargingMode.SILENT, handler.getMode());
		handler.update(1000, OperationalMode.SILENT, 27.0f);
		handler.update(1000 * 1000 * 1000, OperationalMode.SILENT, 26.0f); // make delta time an absurd amount
		handler.update(1000, OperationalMode.SILENT, 25.1f);
		assertEquals(FXChargingMode.SILENT, handler.getMode());
		handler.update(1000, OperationalMode.FLOAT, 25.0f);
		assertEquals(FXChargingMode.REFLOAT, handler.getMode());
		handler.update(1000 * 1000 * 1000, OperationalMode.FLOAT, 26.0f); // make delta time an absurd amount
		assertEquals(FXChargingMode.REFLOAT, handler.getMode());
		handler.update(1000 * 1000 * 1000, OperationalMode.FLOAT, 26.6f);
		assertEquals(FXChargingMode.REFLOAT, handler.getMode());

		handler.update(1000, OperationalMode.FLOAT, 26.8f);
		assertEquals(FXChargingMode.FLOAT, handler.getMode());
		assertEquals(60 * 60 * 1000 * 2 - 1000, handler.getRemainingFloatTimeMillis());
		handler.update(1000, OperationalMode.FLOAT, 26.0f);
		assertEquals(FXChargingMode.FLOAT, handler.getMode());
		handler.update(1000, OperationalMode.FLOAT, 25.1f);
		assertEquals(FXChargingMode.FLOAT, handler.getMode());
		assertEquals(60 * 60 * 1000 * 2 - 3000, handler.getRemainingFloatTimeMillis());
		handler.update(1000, OperationalMode.INV_OFF, 25.1f); // AC gone
		assertNull(handler.getMode());
		assertEquals(60 * 60 * 1000 * 2 - 3000, handler.getRemainingFloatTimeMillis()); // rebulk is not null, so it should keep the timer correct
		handler.update(1000, OperationalMode.FLOAT, 25.1f); // AC back
		assertEquals(FXChargingMode.REFLOAT, handler.getMode());
		assertEquals(60 * 60 * 1000 * 2 - 3000, handler.getRemainingFloatTimeMillis());
		handler.update(1000, OperationalMode.FLOAT, 26.6f);
		assertEquals(FXChargingMode.REFLOAT, handler.getMode());
		handler.update(1000, OperationalMode.FLOAT, 26.8f);
		assertEquals(FXChargingMode.FLOAT, handler.getMode());
		assertEquals(60 * 60 * 1000 * 2 - 4000, handler.getRemainingFloatTimeMillis());
		handler.update(60 * 60 * 1000 * 2 - 4000 - 1, OperationalMode.FLOAT, 26.8f);
		assertEquals(FXChargingMode.FLOAT, handler.getMode());
		handler.update(1, OperationalMode.SILENT, 26.8f);
		assertEquals(FXChargingMode.SILENT, handler.getMode());
	}
}

package me.retrodaredevil.solarthing.solar.outback.fx.charge;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FXChargingStateHandlerTest {

	@Test
	void test(){
		FXChargingSettings settings = new FXChargingSettings(
				null,
				28.8f, 60 * 60 * 1000,
				26.8f, 60 * 60 * 1000 * 2, 25.0f,
				29.2f, 60 * 60 * 1000 * 3
		);
		FXChargingStateHandler handler = new FXChargingStateHandler(settings);
		handler.update(1000, false, false, 23.0f);
		assertEquals(FXChargingMode.BULK, handler.getMode());
		handler.update(1000, true, false, 28.6f);
		assertEquals(FXChargingMode.BULK, handler.getMode());

		handler.update(1000, true, false, 28.8f);
		assertEquals(FXChargingMode.ABSORB_OR_EQ, handler.getMode());
		handler.update(60 * 60 * 1000 - 1001, true, false, 28.8f); // 1 millisecond before timer expires
		assertEquals(FXChargingMode.ABSORB_OR_EQ, handler.getMode());
		handler.update(1, true, false, 28.8f);
		assertEquals(FXChargingMode.SILENT, handler.getMode());

		handler.update(1000, true, false, 28.8f);
		handler.update(1000, true, false, 28.8f);
		assertEquals(FXChargingMode.SILENT, handler.getMode());
		handler.update(1000, true, false, 27.0f);
		handler.update(1000 * 1000 * 1000, true, false, 26.0f); // make delta time an absurd amount
		handler.update(1000, true, false, 25.1f);
		assertEquals(FXChargingMode.SILENT, handler.getMode());
		handler.update(1000, true, false, 25.0f);
		assertEquals(FXChargingMode.REFLOAT, handler.getMode());
		handler.update(1000 * 1000 * 1000, true, false, 26.0f); // make delta time an absurd amount
		assertEquals(FXChargingMode.REFLOAT, handler.getMode());
		handler.update(1000 * 1000 * 1000, true, false, 26.6f);
		assertEquals(FXChargingMode.REFLOAT, handler.getMode());

		handler.update(1000, true, false, 26.8f);
		assertEquals(FXChargingMode.FLOAT, handler.getMode());
		assertEquals(60 * 60 * 1000 * 2 - 1000, handler.getRemainingFloatTimeMillis());
		handler.update(1000, true, false, 26.0f);
		assertEquals(FXChargingMode.FLOAT, handler.getMode());
		handler.update(1000, true, false, 25.1f);
		assertEquals(FXChargingMode.FLOAT, handler.getMode());
		assertEquals(60 * 60 * 1000 * 2 - 3000, handler.getRemainingFloatTimeMillis());
		handler.update(1000, false, false, 25.1f); // AC gone
		assertEquals(FXChargingMode.REFLOAT, handler.getMode());
		assertEquals(60 * 60 * 1000 * 2 - 3000, handler.getRemainingFloatTimeMillis());
		handler.update(1000, true, false, 25.1f); // AC back
		assertEquals(FXChargingMode.REFLOAT, handler.getMode());
		assertEquals(60 * 60 * 1000 * 2 - 3000, handler.getRemainingFloatTimeMillis());
		handler.update(1000, true, false, 26.6f);
		assertEquals(FXChargingMode.REFLOAT, handler.getMode());
		handler.update(1000, true, false, 26.8f);
		assertEquals(FXChargingMode.FLOAT, handler.getMode());
		assertEquals(60 * 60 * 1000 * 2 - 4000, handler.getRemainingFloatTimeMillis());
		handler.update(60 * 60 * 1000 * 2 - 4000 - 1, true, false, 26.8f);
		assertEquals(FXChargingMode.FLOAT, handler.getMode());
		handler.update(1, true, false, 26.8f);
		assertEquals(FXChargingMode.SILENT, handler.getMode());
	}
}

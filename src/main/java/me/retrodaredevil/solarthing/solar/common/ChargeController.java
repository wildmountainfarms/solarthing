package me.retrodaredevil.solarthing.solar.common;

public interface ChargeController extends BatteryVoltage, PVCurrentAndVoltage {
	/**
	 * @return The current in amps this controller is putting into the batteries
	 */
	Number getChargerCurrent();
	
	/**
	 * Normally, this can also be calculated by multiplying {@link #getChargerCurrent()} and {@link #getBatteryVoltage()}
	 * @return The charging power in Watts
	 */
	Number getChargingPower();
	
	/**
	 * This method allows this interface to be compatible with FlexMAX {@link me.retrodaredevil.solarthing.solar.outback.mx.MXStatusPacket}s.
	 * @return [0..0.9] The current to add to {@link #getChargerCurrent()}
	 */
	default Number getAmpChargerCurrent(){
		return 0;
	}
	
	default String getChargerCurrentString(){
		return getChargerCurrent().toString();
	}
}

package me.retrodaredevil.solarthing.solar.common;

public interface ChargeController extends BatteryVoltage, PVCurrentAndVoltage {
	/**
	 * @return The current in amps this controller is putting into the batteries
	 */
	@Deprecated
	default Number getChargerCurrent(){
		return getChargingCurrent();
	}
	
	Number getChargingCurrent();
	
	/**
	 * Normally, this can also be calculated by multiplying {@link #getChargingCurrent()} and {@link #getBatteryVoltage()}
	 * @return The charging power in Watts
	 */
	Number getChargingPower();
	
	/**
	 * This method allows this interface to be compatible with FlexMAX {@link me.retrodaredevil.solarthing.solar.outback.mx.MXStatusPacket}s.
	 * @return [0..0.9] The current to add to {@link #getChargerCurrent()}
	 */
	@Deprecated
	default Number getAmpChargerCurrent(){
		return 0;
	}
	
	/**
	 * Should be serialized as "ampChargerCurrentString" if serialized at all
	 * @see #getAmpChargerCurrent()
	 * @return The amp charger current in the format "0.X" where X is a digit [0..0.9]
	 */
	@Deprecated
	default String getAmpChargerCurrentString(){
		return getAmpChargerCurrent().toString();
	}
	
	@Deprecated
	default String getChargerCurrentString(){
		return getChargerCurrent().toString();
	}
}

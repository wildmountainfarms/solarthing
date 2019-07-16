package me.retrodaredevil.solarthing.solar.common;

public interface ChargeController extends BatteryVoltage, PVCurrentAndVoltage {
	Number getChargerCurrent();
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

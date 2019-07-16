package me.retrodaredevil.solarthing.solar.renogy.rover;

import me.retrodaredevil.solarthing.packets.CodeMode;
import me.retrodaredevil.solarthing.packets.Modes;

public class SpecialPowerControl_E021 {
	
	private final int upper;
	private final int lower;
	
	public SpecialPowerControl_E021(int upper, int lower){
		this.upper = upper;
		this.lower = lower;
	}
	public SpecialPowerControl_E021(int value){
		this(value >>> 8, value & 0xFF);
	}
	
	
	// region upper
	/**
	 *
	 * @return true if charging mode is controller by voltage, false if charging mode controlled by SOC
	 */
	public boolean isChargingModeControlledByVoltage(){
		return (0b100 & upper) != 0;
	}
	/**
	 * @return true if special power control is enabled, false if disabled
	 */
	public boolean isSpecialPowerControlEnabled(){
		return (0b10 & upper) != 0;
	}
	/**
	 *
	 * @return true if the "each night on" function is enabled, false if disabled
	 */
	public boolean isEachNightOnEnabled(){
		return (0b1 & upper) != 0;
	}
	// endregion
	
	// region lower
	/**
	 * @return true if "no charging below 0C" is enabled, false otherwise
	 */
	public boolean isNoChargingBelow0CEnabled(){
		return (0b100 & lower) != 0;
	}
	
	public int getChargingMethodValueCode(){
		return 0b11 & lower;
	}
	public ChargingMethod_E021 getChargingMethod(){
		return Modes.getActiveMode(ChargingMethod_E021.class, getChargingMethodValueCode());
	}
	
	// endregion
	public enum ChargingMethod_E021 implements CodeMode {
		DIRECT(0, ChargingMethod.DIRECT),
		PWM(1, ChargingMethod.PWM)
		;
		
		private final int value;
		private final ChargingMethod chargingMethod;
		
		ChargingMethod_E021(int value, ChargingMethod chargingMethod) {
			this.value = value;
			this.chargingMethod = chargingMethod;
		}
		public ChargingMethod getChargingMethod(){
			return chargingMethod;
		}
		
		@Override
		public int getValueCode() {
			return value;
		}
		
		@Override
		public String getModeName() {
			return chargingMethod.getModeName();
		}
	}
	
	
}

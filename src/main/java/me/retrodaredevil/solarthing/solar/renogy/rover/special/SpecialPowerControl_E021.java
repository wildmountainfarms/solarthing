package me.retrodaredevil.solarthing.solar.renogy.rover.special;

import me.retrodaredevil.solarthing.packets.CodeMode;
import me.retrodaredevil.solarthing.packets.Modes;
import me.retrodaredevil.solarthing.solar.renogy.rover.ChargingMethod;

public interface SpecialPowerControl_E021 extends UpperLower16Bit {
	
	default String getFormattedInfo(){
		return "charging mode controlled by voltage: " + isChargingModeControlledByVoltage() + "\n" +
			"special power control: " + (isSpecialPowerControlEnabled() ? "enabled" : "disabled") + "\n" +
			"each night on: " + (isEachNightOnEnabled() ? "enabled" : "disabled") + "\n" +
			"no charging below 0C: " + (isNoChargingBelow0CEnabled() ? "enabled" : "disabled") + "\n" +
			"charging method: " + getChargingMethod().getModeName();
	}
	
	// upper
	
	/**
	 *
	 * @return true if charging mode is controller by voltage, false if charging mode controlled by SOC
	 */
	default boolean isChargingModeControlledByVoltage(){
		return (0b100 & getUpper()) != 0;
	}
	/**
	 * @return true if special power control is enabled, false if disabled
	 */
	default boolean isSpecialPowerControlEnabled(){
		return (0b10 & getUpper()) != 0;
	}
	/**
	 *
	 * @return true if the "each night on" function is enabled, false if disabled
	 */
	default boolean isEachNightOnEnabled(){
		return (0b1 & getUpper()) != 0;
	}
	
	
	// lower
	
	/**
	 * @return true if "no charging below 0C" is enabled, false otherwise
	 */
	default boolean isNoChargingBelow0CEnabled(){
		return (0b100 & getLower()) != 0;
	}
	
	default int getChargingMethodValueCode(){
		return 0b11 & getLower();
	}
	default ChargingMethod_E021 getChargingMethod(){
		return Modes.getActiveMode(ChargingMethod_E021.class, getChargingMethodValueCode());
	}
	
	enum ChargingMethod_E021 implements CodeMode {
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

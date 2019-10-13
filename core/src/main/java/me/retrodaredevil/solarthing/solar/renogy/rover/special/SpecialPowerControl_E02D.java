package me.retrodaredevil.solarthing.solar.renogy.rover.special;

import me.retrodaredevil.solarthing.packets.CodeMode;
import me.retrodaredevil.solarthing.packets.Modes;
import me.retrodaredevil.solarthing.solar.renogy.rover.ChargingMethod;

public interface SpecialPowerControl_E02D extends UpperLower16Bit {
	
	default String getFormattedInfo(){
		return "intelligent power: " + (isIntelligentPowerEnabled() ? "enabled" : "disabled") + "\n" +
			"each night on: " + (isEachNightOnEnabled() ? "enabled" : "disabled") + "\n" +
			"battery type: " + getBatteryType().getModeName() + "\n" +
			"charging method: " + getChargingMethod().getModeName() + "\n" +
			"no charging below 0C: " + (isNoChargingBelow0CEnabled() ? "enabled" : "disabled") + "\n" +
			"system voltage: " + getSystemVoltage().getModeName();
	}
	
	default boolean isIntelligentPowerEnabled(){
		return (0b10 & getUpper()) != 0;
	}
	
	default boolean isEachNightOnEnabled(){
		return (0b1 & getUpper()) != 0;
	}
	
	default int getBatteryTypeValueCode(){
		return (0b11110000 & getLower()) >>> 4;
	}
	default BatteryType getBatteryType(){
		return Modes.getActiveMode(BatteryType.class, getBatteryTypeValueCode());
	}
	default boolean isLithiumBattery(){
		switch(getBatteryType()){
			case LITHIUM: return true;
			case LEAD_ACID: return false;
			default: throw new UnsupportedOperationException();
		}
	}
	default int getChargingMethodValueCode(){
		return (0b1000 & getLower()) >>> 3;
	}
	default ChargingMethod_E02D getChargingMethod(){
		return Modes.getActiveMode(ChargingMethod_E02D.class, getChargingMethodValueCode());
	}
	
	/**
	 * @return true if "no charging below 0C" is enabled, false otherwise
	 */
	default boolean isNoChargingBelow0CEnabled(){
		return (0b100 & getLower()) != 0;
	}
	
	default int getSystemVoltageValueCode(){
		return 0b11 & getLower();
	}
	default SystemVoltage getSystemVoltage(){
		return Modes.getActiveMode(SystemVoltage.class, getSystemVoltageValueCode());
	}
	default boolean is24VSystem(){
		return getSystemVoltage() == SystemVoltage.V24;
	}
	
	enum BatteryType implements CodeMode {
		LEAD_ACID("lead-acid", 0),
		LITHIUM("lithium", 1);
		
		private final String name;
		private final int value;
		
		BatteryType(String name, int value) {
			this.name = name;
			this.value = value;
		}
		
		@Override
		public int getValueCode() {
			return value;
		}
		
		@Override
		public String getModeName() {
			return name;
		}
	}
	enum ChargingMethod_E02D implements CodeMode {
		PWM(0, ChargingMethod.PWM),
		DIRECT(1, ChargingMethod.DIRECT),
		;
		
		private final int value;
		private final ChargingMethod chargingMethod;
		
		ChargingMethod_E02D(int value, ChargingMethod chargingMethod) {
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
		public int getIgnoredBits() {
			return 0b11111100;
		}
		
		@Override
		public String getModeName() {
			return chargingMethod.getModeName();
		}
	}
	/**
	 * For use with PDU Address 0xE02D, lower 8 bits, (b1 to b0)
	 */
	enum SystemVoltage implements CodeMode {
		V12(12, 0),
		V24(24, 1)
		;
		
		private final int voltage;
		private final int code;
		
		SystemVoltage(int voltage, int code) {
			this.voltage = voltage;
			this.code = code;
		}
		
		@Override
		public int getValueCode() {
			return code;
		}
		
		@Override
		public String getModeName() {
			return voltage + "V";
		}
	}
}

package me.retrodaredevil.solarthing.solar.renogy.rover;

import me.retrodaredevil.solarthing.packets.CodeMode;
import me.retrodaredevil.solarthing.packets.Modes;
import me.retrodaredevil.solarthing.solar.renogy.SystemVoltage;

public class SpecialPowerControl_E02D {
	
	private final int upper;
	private final int lower;
	
	public SpecialPowerControl_E02D(int upper, int lower){
		this.upper = upper;
		this.lower = lower;
	}
	public SpecialPowerControl_E02D(int value){
		this(value >> 8, value & 0xFF);
	}
	
	public boolean isIntelligentPowerEnabled(){
		return (0b10 & upper) != 0;
	}
	
	public boolean isEachNightOnEnabled(){
		return (0b1 & upper) != 0;
	}
	
	public int getBatteryTypeValueCode(){
		return 0b11110000 & lower;
	}
	public BatteryType getBatteryType(){
		return Modes.getActiveMode(BatteryType.class, getBatteryTypeValueCode());
	}
	
	public enum BatteryType implements CodeMode {
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
	
	public int getChargingMethodValueCode(){
		return 0b1000 & lower;
	}
	public ChargingMethod_E02D getChargingMethod(){
		return Modes.getActiveMode(ChargingMethod_E02D.class, getChargingMethodValueCode());
	}
	public enum ChargingMethod_E02D implements CodeMode {
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
	 * @return true if "no charging below 0C" is enabled, false otherwise
	 */
	public boolean isNoChargingBelow0CEnabled(){
		return (0b100 & lower) != 0;
	}
	
	public int getSystemVoltageValueCode(){
		return 0b11 & lower;
	}
	public SystemVoltage getSystemVoltage(){
		return Modes.getActiveMode(SystemVoltage.class, getSystemVoltageValueCode());
	}
}

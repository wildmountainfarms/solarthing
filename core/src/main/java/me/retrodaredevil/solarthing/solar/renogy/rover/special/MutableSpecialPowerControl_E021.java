package me.retrodaredevil.solarthing.solar.renogy.rover.special;

import me.retrodaredevil.solarthing.solar.renogy.rover.ChargingMethod;

public class MutableSpecialPowerControl_E021 implements SpecialPowerControl_E021{
	private int upper;
	private int lower;
	
	public MutableSpecialPowerControl_E021(int upper, int lower){
		this.upper = upper;
		this.lower = lower;
	}
	public MutableSpecialPowerControl_E021(int value){
		this(value >>> 8, value & 0xFF);
	}
	public MutableSpecialPowerControl_E021(){
		this(0, 0);
	}
	
	public void setChargingModeControlledByVoltage(boolean b){
		upper = (upper & ~0b100) | ((b ? 1 : 0) << 2);
	}
	public void setSpecialPowerControlEnabled(boolean enabled){
		upper = (upper & ~0b10) | ((enabled ? 1 : 0) << 1);
	}
	public void setEachNightOnEnabled(boolean enabled){
		upper = (upper & ~0b1) | (enabled ? 1 : 0);
	}
	
	public void setNoChargingBelow0CEnabled(boolean enabled){
		lower = (lower & ~0b100) | ((enabled ? 1 : 0) << 2);
	}
	public void setChargingMethod(ChargingMethod chargingMethod){
		switch(chargingMethod){
			case DIRECT:
				setChargingMethod(ChargingMethod_E021.DIRECT);
				break;
			case PWM:
				setChargingMethod(ChargingMethod_E021.PWM);
				break;
			default:
				throw new UnsupportedOperationException();
		}
	}
	public void setChargingMethod(ChargingMethod_E021 chargingMethod){
		lower = (lower & ~0b11) | chargingMethod.getValueCode();
	}
	
	
	@Override
	public int getUpper() {
		return upper;
	}
	
	@Override
	public int getLower() {
		return lower;
	}
}

package me.retrodaredevil.solarthing.solar.renogy.rover.special;

import me.retrodaredevil.solarthing.solar.renogy.rover.ChargingMethod;

public class MutableSpecialPowerControl_E02D implements SpecialPowerControl_E02D {
	private int upper;
	private int lower;

	public MutableSpecialPowerControl_E02D(int upper, int lower){
		this.upper = upper;
		this.lower = lower;
	}
	public MutableSpecialPowerControl_E02D(SpecialPowerControl_E02D specialPowerControl_e02D){
		this(specialPowerControl_e02D.getUpper(), specialPowerControl_e02D.getLower());
	}
	public MutableSpecialPowerControl_E02D(){
		this(0, 0);
	}

	public void setIntelligentPowerEnabled(boolean enabled){
		if(enabled){
			upper |= 0b10;
		} else {
			upper = upper & ~0b10;
		}
	}
	public void setEachNightOnEnabled(boolean enabled){
		if(enabled){
			upper |= 0b1;
		} else {
			upper = upper & ~0b1;
		}
	}
	public void setBatteryType(BatteryType batteryType){
		int code = batteryType.getValueCode();
		lower = (lower & ~0b11110000) | (code << 4);
	}
	public void setIsLithiumBattery(boolean isLithiumBattery){
		setBatteryType(isLithiumBattery ? BatteryType.LITHIUM : BatteryType.LEAD_ACID);
	}
	public void setChargingMethod(ChargingMethod chargingMethod){
		final ChargingMethod_E02D method;
		switch (chargingMethod){
			case PWM:
				method = ChargingMethod_E02D.PWM;
				break;
			case DIRECT:
				method = ChargingMethod_E02D.DIRECT;
				break;
			default:
				throw new UnsupportedOperationException();
		}
		setChargingMethod(method);
	}
	public void setChargingMethod(ChargingMethod_E02D chargingMethod){
		lower = (lower & ~0b1000) | (chargingMethod.getValueCode() << 3);
	}
	public void setNoChargingBelow0CEnabled(boolean enabled){
		int code = enabled ? 1 : 0;
		lower = (lower & ~0b100) | (code << 2);
	}
	public void setSystemVoltage(SystemVoltage systemVoltage){
		lower = (lower & ~0b11) | systemVoltage.getValueCode();
	}
	public void setIs24VSystem(boolean is24VSystem){
		setSystemVoltage(is24VSystem ? SystemVoltage.V24 : SystemVoltage.V12);
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

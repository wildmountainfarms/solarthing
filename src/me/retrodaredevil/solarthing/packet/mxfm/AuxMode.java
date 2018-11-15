package me.retrodaredevil.solarthing.packet.mxfm;

import me.retrodaredevil.solarthing.packet.CodeMode;

public enum AuxMode implements CodeMode {
	UNKNOWN(-1, "unknown"),
	DISABLED(0, "disabled"),
	DIVERSION(1, "Diversion"),
	REMOTE(2, "Remote"),
	MANUAL(3, "Manual"),
	VENT_FAN(4, "Vent Fan"),
	PV_TRIGGER(5, "PV Trigger"),
	FLOAT(6, "Float"),
	ERROR_OUTPUT(7, "ERROR Output"),
	NIGHT_LIGHT(8, "Night Light"),
	PWM_DIVERSION(9, "PWM Diversion"),
	LOW_BATTERY(10, "Low Battery");

	private final int value;
	private final String name;
	AuxMode(int value, String name){
		this.value = value;
		this.name = name;
	}

	@Override
	public String getModeName() {
        return name;
	}

	@Override
	public String toString() {
        return name;
	}

	@Override
	public int getValueCode() {
        return value;
	}

}


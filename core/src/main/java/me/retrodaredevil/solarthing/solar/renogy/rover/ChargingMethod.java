package me.retrodaredevil.solarthing.solar.renogy.rover;

public enum ChargingMethod {
	DIRECT("Direct"),
	PWM("PWM")
	;

	private final String name;

	ChargingMethod(String name) {
		this.name = name;
	}

	public String getModeName() {
		return name;
	}
}

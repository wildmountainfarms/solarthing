package me.retrodaredevil.solarthing.solar.outback.fx.charge;

public class FXChargingSettings {
	private final Float rebulkVoltage;

	private final float absorbVoltage;
	private final long absorbTimeMillis;

	private final float floatVoltage;
	private final long floatTimeMillis;
	private final float refloatVoltage;

	private final float equalizeVoltage;
	private final long equalizeTimeMillis;

	public FXChargingSettings(
			Float rebulkVoltage, float absorbVoltage, long absorbTimeMillis,
			float floatVoltage, long floatTimeMillis, float refloatVoltage,
			float equalizeVoltage, long equalizeTimeMillis) {
		this.rebulkVoltage = rebulkVoltage;
		this.absorbVoltage = absorbVoltage;
		this.absorbTimeMillis = absorbTimeMillis;
		this.floatVoltage = floatVoltage;
		this.floatTimeMillis = floatTimeMillis;
		this.refloatVoltage = refloatVoltage;
		this.equalizeVoltage = equalizeVoltage;
		this.equalizeTimeMillis = equalizeTimeMillis;
	}

	public boolean isFloatTimerStartedImmediately(){
		// TODO even on some?|all? newer firmwares the float timer starts immediately once the setpoint is reached
		return rebulkVoltage == null; // if we're on older firmware without rebulk voltage, this also means that the float timer is started immediately
	}

	public Float getRebulkVoltage() {
		return rebulkVoltage;
	}

	public float getAbsorbVoltage() {
		return absorbVoltage;
	}

	public long getAbsorbTimeMillis() {
		return absorbTimeMillis;
	}

	public float getFloatVoltage() {
		return floatVoltage;
	}

	public long getFloatTimeMillis() {
		return floatTimeMillis;
	}

	public float getRefloatVoltage() {
		return refloatVoltage;
	}

	public float getEqualizeVoltage() {
		return equalizeVoltage;
	}

	public long getEqualizeTimeMillis() {
		return equalizeTimeMillis;
	}
}

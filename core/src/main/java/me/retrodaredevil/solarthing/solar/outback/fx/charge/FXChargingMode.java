package me.retrodaredevil.solarthing.solar.outback.fx.charge;

import org.jspecify.annotations.NullMarked;

@NullMarked
public enum FXChargingMode {
	BULK_TO_ABSORB,
	BULK_TO_EQ,
	ABSORB,
	EQ,
	SILENT,
	/** AKA Bulk to Float */
	REFLOAT,
	FLOAT
}

package me.retrodaredevil.solarthing;

public enum InstantType {
	INSTANT,
	NOT_INSTANT;


	public boolean isInstant() {
		return this == INSTANT;
	}
}

package me.retrodaredevil.solarthing;

@Deprecated
public enum InstantType {
	INSTANT,
	NOT_INSTANT;


	public boolean isInstant() {
		return this == INSTANT;
	}
}

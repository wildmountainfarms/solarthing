package me.retrodaredevil.solarthing;

/**
 * Represents something that handles when data is received, without knowing what that data is, just knowing that when this
 * method is called, data has been received.
 */
public interface OnDataReceive {
	void onDataReceive(boolean firstData, boolean wasInstant);
	class Defaults {
		public static final OnDataReceive NOTHING = (firstData, wasInstant) -> {};
	}
}

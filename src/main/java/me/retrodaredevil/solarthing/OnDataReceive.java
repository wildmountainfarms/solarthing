package me.retrodaredevil.solarthing;

public interface OnDataReceive {
	void onDataReceive(boolean firstData, boolean wasInstant);
	class Defaults {
		public static final OnDataReceive NOTHING = (firstData, wasInstant) -> {};
	}
}

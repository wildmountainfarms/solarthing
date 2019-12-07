package me.retrodaredevil.solarthing;

/**
 * Represents something that handles when data is received, without knowing what that data is, just knowing that when this
 * method is called, data has been received.
 */
public interface OnDataReceive {
	/**
	 *
	 * @param firstData true if the data being received is the first data of a set of data to come in the future. false otherwise
	 * @param wasInstant true if the data is from right now. false if the data has been piling up.
	 */
	void onDataReceive(boolean firstData, boolean wasInstant);
	class Defaults {
		public static final OnDataReceive NOTHING = (firstData, wasInstant) -> {};
	}
}

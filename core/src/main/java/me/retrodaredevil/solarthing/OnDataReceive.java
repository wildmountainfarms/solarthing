package me.retrodaredevil.solarthing;

/**
 * Represents something that handles when data is received, without knowing what that data is, just knowing that when this
 * method is called, data has been received.
 */
public interface OnDataReceive {
	/**
	 *
	 * @param firstData true if the data being received is the first data of a set of data to come in the future. false otherwise
	 */
	void onDataReceive(boolean firstData, boolean stale);
	class Defaults {
		public static final OnDataReceive NOTHING = (firstData, stale) -> {};

		private Defaults() { throw new UnsupportedOperationException(); }
	}
}

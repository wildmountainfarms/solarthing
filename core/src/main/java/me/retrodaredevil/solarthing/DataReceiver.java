package me.retrodaredevil.solarthing;

/**
 * Represents something that handles the event of receiving data from a sender at a certain time
 */
public interface DataReceiver {
	void receiveData(String sender, long dateMillis, String data);
}

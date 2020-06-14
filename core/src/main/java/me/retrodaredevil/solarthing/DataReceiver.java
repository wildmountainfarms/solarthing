package me.retrodaredevil.solarthing;

/**
 * Represents something that handles the event of receiving data from a sender at a certain time
 */
@Deprecated
public interface DataReceiver {
	@Deprecated
	void receiveData(String sender, long dateMillis, String data);
}

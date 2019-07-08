package me.retrodaredevil.solarthing;

public interface DataReceiver {
	void receiveData(String sender, long dateMillis, String data);
}

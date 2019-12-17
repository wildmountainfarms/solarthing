package me.retrodaredevil.solarthing.pvoutput;

public final class SimpleTime implements PVOutputString {
	private final int hour;
	private final int minute;

	public SimpleTime(int hour, int minute) {
		this.hour = hour;
		this.minute = minute;
	}

	public int getHour() {
		return hour;
	}

	public int getMinute() {
		return minute;
	}

	@Override
	public String toPVOutputString() {
		String hourString = "" + hour;
		if(hourString.length() < 2){
			hourString = "0" + hourString;
		}
		String minuteString = "" + minute;
		if(minuteString.length() < 2){
			minuteString = "0" + minuteString;
		}
		return hourString + ":" + minuteString;
	}
}

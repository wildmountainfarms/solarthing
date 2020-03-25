package me.retrodaredevil.solarthing.solar.outback.fx.charge;

public class ModeTimer {
	private final long modeTimeMillis;
	private long timeMillis;

	public ModeTimer(long modeTimeMillis) {
		this.modeTimeMillis = modeTimeMillis;
		resetTimer();
	}
	public void resetTimer(){
		timeMillis = modeTimeMillis;
	}
	public void setToZero(){
		timeMillis = 0;
	}
	public boolean isDone(){
		return timeMillis == 0;
	}
	public long getRemainingTimeMillis(){
		return timeMillis;
	}
	public void countDown(long millis){
		timeMillis = Math.max(timeMillis - millis, 0L);
	}
	public void countUp(long millis){
		timeMillis = Math.min(timeMillis + millis, modeTimeMillis);
	}
}

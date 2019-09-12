package me.retrodaredevil.solarthing.packets.handling;

public final class FrequencySettings {
	
	public static final FrequencySettings NORMAL_SETTINGS = new FrequencySettings(1, 0);
	
	private final int throttleFactor;
	private final int initialSkipFactor;
	
	public FrequencySettings(int throttleFactor, int initialSkipFactor) {
		this.throttleFactor = throttleFactor;
		this.initialSkipFactor = initialSkipFactor;
		if(throttleFactor < 1){
			throw new IllegalArgumentException("Throttle factor must be >= 1");
		}
		if(initialSkipFactor < 0){
			throw new IllegalArgumentException("Initial skip factor must be >= 0");
		}
	}
	
	public int getThrottleFactor() {
		return throttleFactor;
	}
	
	public int getInitialSkipFactor() {
		return initialSkipFactor;
	}
}

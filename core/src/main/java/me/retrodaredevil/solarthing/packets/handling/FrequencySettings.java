package me.retrodaredevil.solarthing.packets.handling;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = FrequencySettings.Builder.class)
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

	@JsonPOJOBuilder
	static class Builder {
		private int throttleFactor = 1;
		private int initialSkipFactor = 0;

		@JsonSetter("throttle_factor")
		public void setThrottleFactor(int throttleFactor) {
			this.throttleFactor = throttleFactor;
		}

		@JsonSetter("initial_skip")
		public void setInitialSkipFactor(int initialSkipFactor) {
			this.initialSkipFactor = initialSkipFactor;
		}
		public FrequencySettings build(){
			return new FrequencySettings(throttleFactor, initialSkipFactor);
		}
	}
}

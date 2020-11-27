package me.retrodaredevil.solarthing.homeassistant.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.Map;

public class SensorInfo implements HomeAssistantSensorInfo {
	private final String entityId;
	private final Instant lastChanged;
	private final Instant lastUpdated;
	private final Map<String, String> context;
	private final String state;
	private final Map<String, String> attributes;

	@JsonCreator
	public SensorInfo(
			@JsonProperty("entity_id") String entityId,
			@JsonProperty("last_changed") Instant lastChanged, @JsonProperty("last_updated") Instant lastUpdated,
			@JsonProperty("context") Map<String, String> context,
			@JsonProperty("state") String state,
			@JsonProperty("attributes") Map<String, String> attributes) {
		this.entityId = entityId;
		this.lastChanged = lastChanged;
		this.lastUpdated = lastUpdated;
		this.context = context;
		this.state = state;
		this.attributes = attributes;
	}

	@Override
	public String getEntityId() {
		return entityId;
	}

	@Override
	public Instant getLastChanged() {
		return lastChanged;
	}

	@Override
	public Instant getLastUpdated() {
		return lastUpdated;
	}

	@Override
	public Map<String, String> getContext() {
		return context;
	}

	@Override
	public String getState() {
		return state;
	}

	@Override
	public Map<String, String> getAttributes() {
		return attributes;
	}

	@Override
	public String toString() {
		return "SensorInfo(" +
				"entityId='" + entityId + '\'' +
				", lastChanged=" + lastChanged +
				", lastUpdated=" + lastUpdated +
				", context=" + context +
				", state='" + state + '\'' +
				", attributes=" + attributes +
				')';
	}
}

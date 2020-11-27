package me.retrodaredevil.solarthing.homeassistant.data;

import java.time.Instant;
import java.util.Map;

public interface HomeAssistantSensorInfo extends HomeAssistantSensorState {
	String getEntityId();
	default String getName() {
		return getEntityId().split("\\.", 2)[1];
	}
	Instant getLastChanged();
	Instant getLastUpdated();
	Map<String, String> getContext();
}

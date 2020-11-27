package me.retrodaredevil.solarthing.homeassistant.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;

import java.util.Map;

@JsonExplicit
public interface HomeAssistantSensorState {
	@JsonProperty("state")
	String getState();

	@JsonProperty("attributes")
	Map<String, String> getAttributes();

}

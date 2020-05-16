package me.retrodaredevil.solarthing.config.options;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@JsonTypeName("pvoutput-upload")
@JsonExplicit
public class PVOutputUploadProgramOptions extends DatabaseTimeZoneOptionBase implements AnalyticsOption, DatabaseOption, ProgramOptions {
	@JsonProperty(value = "system_id", required = true)
	private int systemId;
	@JsonProperty(value = "api_key", required = true)
	private String apiKey;

	@JsonProperty("required")
	private Map<Integer, List<String>> requiredIdentifierMap = null;

	@JsonProperty(AnalyticsOption.PROPERTY_NAME)
	private boolean isAnalyticsEnabled = AnalyticsOption.DEFAULT_IS_ANALYTICS_ENABLED;

	@Override
	public ProgramType getProgramType() {
		return ProgramType.PVOUTPUT_UPLOAD;
	}
	@Override
	public boolean isAnalyticsEnabled() {
		return isAnalyticsEnabled;
	}

	public int getSystemId() {
		return systemId;
	}

	public String getApiKey() {
		return apiKey;
	}

	public Map<Integer, List<String>> getRequiredIdentifierMap() {
		Map<Integer, List<String>> r = requiredIdentifierMap;
		if (r == null) {
			return Collections.emptyMap();
		}
		return r;
	}
}

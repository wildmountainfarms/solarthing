package me.retrodaredevil.solarthing.config.options;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.packets.collection.DefaultInstanceOptions;
import me.retrodaredevil.solarthing.packets.instance.InstanceSourcePacket;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

@JsonTypeName("pvoutput-upload")
@JsonExplicit
public class PVOutputUploadProgramOptions implements AnalyticsOption, ProgramOptions {
	@JsonProperty(value = "system_id", required = true)
	private int systemId;
	@JsonProperty(value = "api_key", required = true)
	private String apiKey;
	@JsonProperty(value = "database", required = true)
	private File database;
	@JsonProperty("time_zone")
	private TimeZone timeZone = null;
	@JsonProperty(value = "source", required = true)
	private String sourceId = null;

	@JsonProperty("include_undefined_sources")
	private Boolean includeUndefinedSources = null;
	@JsonProperty("default_fragment")
	private Integer defaultFragment = null;

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

	public File getDatabase() {
		return database;
	}
	public TimeZone getTimeZone(){
		TimeZone r = this.timeZone;
		if(r == null){
			return TimeZone.getDefault();
		}
		return r;
	}
	public String getSourceId(){
		return sourceId;
	}

	public Map<Integer, List<String>> getRequiredIdentifierMap() {
		Map<Integer, List<String>> r = requiredIdentifierMap;
		if (r == null) {
			return Collections.emptyMap();
		}
		return r;
	}
	private String getDefaultSourceId() {
		Boolean includeUndefinedSources = this.includeUndefinedSources;
		if (Boolean.TRUE.equals(includeUndefinedSources)) {
			return sourceId;
		}
		if (Boolean.FALSE.equals(includeUndefinedSources)) {
			return InstanceSourcePacket.UNUSED_SOURCE_ID;
		}
		return InstanceSourcePacket.DEFAULT_SOURCE_ID;
	}
	public DefaultInstanceOptions getDefaultInstanceOptions() {
		return new DefaultInstanceOptions(getDefaultSourceId(), defaultFragment);
	}
}

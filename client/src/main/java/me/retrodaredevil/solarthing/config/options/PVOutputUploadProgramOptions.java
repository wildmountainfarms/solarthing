package me.retrodaredevil.solarthing.config.options;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

@JsonTypeName("pvoutput-upload")
@JsonExplicit
public class PVOutputUploadProgramOptions implements ProgramOptions {
	@JsonProperty(value = "system_id", required = true)
	private int systemId;
	@JsonProperty(value = "api_key", required = true)
	private String apiKey;
	@JsonProperty(value = "database", required = true)
	private File database;
	@JsonProperty("time_zone")
	private TimeZone timeZone = null;
	@JsonProperty("source")
	private String sourceId = null;

	@JsonProperty("required_fragment_ids")
	private List<Integer> requiredFragmentIds = null;
	@JsonProperty("required_identifiers")
	private Map<Integer, String> requiredIdentifiers = null;

	@Override
	public ProgramType getProgramType() {
		return ProgramType.PVOUTPUT_UPLOAD;
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

	public List<Integer> getRequiredFragmentIds() {
		List<Integer> r = requiredFragmentIds;
		if (r == null) {
			return Collections.emptyList();
		}
		return r;
	}

	public Map<Integer, String> getRequiredIdentifiers() {
		Map<Integer, String> r = requiredIdentifiers;
		if (r == null) {
			return Collections.emptyMap();
		}
		return r;
	}
}

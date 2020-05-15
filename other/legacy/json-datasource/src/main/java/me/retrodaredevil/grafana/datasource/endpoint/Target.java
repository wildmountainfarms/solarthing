package me.retrodaredevil.grafana.datasource.endpoint;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Target {
	/** Represents the 'Additional JSON Data'. May be null */
	@JsonDeserialize
	private final Map<String, Object> data;
	@JsonDeserialize
	private final String refId;
	@JsonDeserialize
	private final String target;

	private Target(){
		data = null;
		refId = "";
		target = "";
	}

	public Target(Map<String, Object> data, String refId, String target) {
		this.data = data;
		this.refId = refId;
		this.target = target;
	}

	public Map<String, Object> getAdditionalData(){
		return data;
	}

	public String getRefId() {
		return refId;
	}

	public String getTarget() {
		return target;
	}

	@Override
	public String toString() {
		return "Target(" +
				"refId='" + refId + '\'' +
				", target='" + target + '\'' +
				')';
	}
}

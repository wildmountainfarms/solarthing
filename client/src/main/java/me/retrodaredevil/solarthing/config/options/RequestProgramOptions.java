package me.retrodaredevil.solarthing.config.options;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.config.request.DataRequester;

import java.util.List;

@JsonTypeName("request")
public class RequestProgramOptions extends PacketHandlingOptionBase implements AnalyticsOption {
	@JsonProperty(AnalyticsOption.PROPERTY_NAME)
	private boolean isAnalyticsEnabled = AnalyticsOption.DEFAULT_IS_ANALYTICS_ENABLED;

	@JsonProperty(value = "request", required = true)
	private List<DataRequester> dataRequesterList;

	@JsonProperty("period")
	private float periodSeconds = 5.0f;
	@JsonProperty("minimum_wait")
	private float minimumWaitSeconds = 1.0f;

	@Override
	public boolean isAnalyticsOptionEnabled() {
		return isAnalyticsEnabled;
	}

	@Override
	public ProgramType getProgramType() {
		return ProgramType.REQUEST;
	}

	public List<DataRequester> getDataRequesterList() {
		return dataRequesterList;
	}
	public long getPeriod() {
		return Math.round(periodSeconds * 1000.0f);
	}
	public long getMinimumWait() {
		return Math.round(minimumWaitSeconds * 1000.0f);
	}
}

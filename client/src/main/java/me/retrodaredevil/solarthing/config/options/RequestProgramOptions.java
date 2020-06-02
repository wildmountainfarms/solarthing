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

	@Override
	public boolean isAnalyticsEnabled() {
		return isAnalyticsEnabled;
	}

	@Override
	public ProgramType getProgramType() {
		return ProgramType.REQUEST;
	}

	public List<DataRequester> getDataRequesterList() {
		return dataRequesterList;
	}
}

package me.retrodaredevil.solarthing.config.options;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;

@JsonTypeName("mattermost")
@JsonExplicit
public class MattermostProgramOptions extends DatabaseTimeZoneOptionBase {
	@JsonProperty(value = "url", required = true)
	private String url;
	@JsonProperty(value = "token", required = true)
	private String token;
	@JsonProperty(value = "channel_id", required = true)
	private String channelId;

	@Override
	public ProgramType getProgramType() {
		return ProgramType.MATTERMOST;
	}

	public String getUrl() { return url; }
	public String getChannelId() { return channelId; }

	public String getToken() {
		return token;
	}
}

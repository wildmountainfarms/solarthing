package me.retrodaredevil.solarthing.message.implementations;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import me.retrodaredevil.solarthing.message.MessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@JsonTypeName("slack")
public class SlackMessageSender implements MessageSender {
	/*
	When setting up in Slack, make sure your bot has the "chat:write" permission and "chat:write.public" permission
	 */

	private static final Logger LOGGER = LoggerFactory.getLogger(SlackMessageSender.class);

	private final String authToken;
	private final String channelId;

	private final Slack slack = Slack.getInstance();

	public SlackMessageSender(@JsonProperty("token") String authToken, @JsonProperty("channel_id") String channelId) {
		this.authToken = authToken;
		this.channelId = channelId;
	}

	@Override
	public void sendMessage(String message) {
		try {
			slack.methods(authToken).chatPostMessage(req -> req.channel(channelId).text(message));
		} catch (IOException | SlackApiException e) {
			LOGGER.error("Could not connect to slack.", e);
		}
	}
}

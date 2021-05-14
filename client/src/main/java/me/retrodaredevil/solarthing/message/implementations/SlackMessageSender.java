package me.retrodaredevil.solarthing.message.implementations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.slack.api.Slack;
import com.slack.api.SlackConfig;
import com.slack.api.methods.SlackApiException;
import com.slack.api.util.http.SlackHttpClient;
import me.retrodaredevil.solarthing.message.MessageSender;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@JsonTypeName("slack")
public class SlackMessageSender implements MessageSender {
	/*
	When setting up in Slack, make sure your bot has the "chat:write" permission and "chat:write.public" permission
	 */

	private static final Logger LOGGER = LoggerFactory.getLogger(SlackMessageSender.class);

	private final String authToken;
	private final String channelId;


	private final Slack slack = Slack.getInstance(new SlackConfig(), new SlackHttpClient(new OkHttpClient.Builder()
			.callTimeout(Duration.ofSeconds(10))
			.connectTimeout(Duration.ofSeconds(3))
//			.addInterceptor(new UserAgentInterceptor(Collections.emptyMap()))
			.build()));
//	private final Slack slack = Slack.getInstance();

	private final ExecutorService executorService = Executors.newSingleThreadExecutor();

	@JsonCreator
	public SlackMessageSender(@JsonProperty("token") String authToken, @JsonProperty("channel_id") String channelId) {
		this.authToken = authToken;
		this.channelId = channelId;
	}

	@Override
	public void sendMessage(String message) {
		send(message, 0);
	}
	private void send(String message, int depth) {
		if (depth >= 5) {
			LOGGER.error("We tried sending '" + message + "' 5 times! Won't try again!");
			return;
		}
		executorService.execute(() -> {
			try {
				slack.methods(authToken).chatPostMessage(req -> req.channel(channelId).text(message));
			} catch (IOException | SlackApiException e) {
				LOGGER.error("Could not connect to slack.", e);
				send(message, depth + 1);
			}
		});
	}
}

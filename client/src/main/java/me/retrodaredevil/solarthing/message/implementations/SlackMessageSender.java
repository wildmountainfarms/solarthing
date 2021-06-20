package me.retrodaredevil.solarthing.message.implementations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.slack.api.Slack;
import com.slack.api.SlackConfig;
import com.slack.api.methods.MethodsClient;
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

import static java.util.Objects.requireNonNull;

@JsonTypeName("slack")
public class SlackMessageSender implements MessageSender {
	/*
	When setting up in Slack, make sure your bot has the "chat:write" permission and "chat:write.public" permission
	 */

	private static final Logger LOGGER = LoggerFactory.getLogger(SlackMessageSender.class);

	private final String channelId;
	private final MethodsClient methodsClient;
	private final ExecutorService executorService = Executors.newSingleThreadExecutor();

	@JsonCreator
	public SlackMessageSender(@JsonProperty("token") String authToken, @JsonProperty("channel_id") String channelId) {
		requireNonNull(authToken);
		requireNonNull(this.channelId = channelId);

		Slack slack = Slack.getInstance(new SlackConfig(), new SlackHttpClient(new OkHttpClient.Builder()
				.callTimeout(Duration.ofSeconds(10))
				.connectTimeout(Duration.ofSeconds(4))
//				.addInterceptor(new UserAgentInterceptor(Collections.emptyMap()))
				.build()));
		methodsClient = slack.methods(authToken);
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
			if (depth > 0) {
				// If we've failed before, start waiting a tiny bit longer before doing another request.
				//   This will also stop other messages from being sent, but that's OK because if we failed for some reason, we don't need other messages being sent. They can wait too.
				try {
					if (depth == 4) {
						Thread.sleep(7000); // if we're about to fail for the last time, just wait a bit longer
					} else {
						Thread.sleep(depth * 700L);
					}
				} catch (InterruptedException e) {
					LOGGER.error("Interrupted", e);
					return;
				}
			}
			try {
				methodsClient.chatPostMessage(req -> req.channel(channelId).text(message));
			} catch (IOException | SlackApiException e) {
				LOGGER.error("Could not connect to slack.", e);
				send(message, depth + 1);
			}
		});
	}
}

package me.retrodaredevil.solarthing.message.implementations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.message.MessageSender;
import net.bis5.mattermost.client4.ApiResponse;
import net.bis5.mattermost.client4.MattermostClient;
import net.bis5.mattermost.client4.model.ApiError;
import net.bis5.mattermost.model.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

@JsonTypeName("mattermost")
public class MattermostMessageSender implements MessageSender {
	private static final Logger LOGGER = LoggerFactory.getLogger(MattermostMessageSender.class);

	private final MattermostClient client;
	private final String channelId;

	public MattermostMessageSender(MattermostClient client, String channelId) {
		this.client = client;
		this.channelId = channelId;
	}
	@JsonCreator
	public static MattermostMessageSender create(
			@JsonProperty(value = "url", required = true) String url,
			@JsonProperty(value = "token", required = true) String token,
			@JsonProperty(value = "channel_id", required = true) String channelId,
			@JsonProperty("connection_timeout") Float connectionTimeoutSeconds,
			@JsonProperty("read_timeout") Float readTimeoutSeconds
	) {
		java.util.logging.Logger.getLogger("").setLevel(Level.OFF);
		MattermostClient client = MattermostClient.builder()
				.url(url)
				.ignoreUnknownProperties()
				.logLevel(Level.INFO)
				.httpConfig(httpClient -> {
					if (connectionTimeoutSeconds != null) {
						httpClient.connectTimeout(Math.round(connectionTimeoutSeconds * 1000), TimeUnit.MILLISECONDS);
					}
					if (readTimeoutSeconds != null) {
						httpClient.readTimeout(Math.round(readTimeoutSeconds * 1000), TimeUnit.MILLISECONDS);
					}
				})
				.build();
		client.setAccessToken(token);
		return new MattermostMessageSender(client, channelId);
	}

	@Override
	public void sendMessage(String message) {
		Post post = new Post();
		post.setChannelId(channelId);
		post.setMessage(message);

		ApiResponse<Post> response = client.createPost(post);
		ApiError error = response.readError();
		if (error != null && error.getDetailedError() != null) {
			LOGGER.info("error: " + error.getDetailedError());
		}
	}
}

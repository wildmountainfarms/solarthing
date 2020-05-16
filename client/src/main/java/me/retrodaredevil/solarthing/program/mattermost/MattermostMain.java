package me.retrodaredevil.solarthing.program.mattermost;

import me.retrodaredevil.solarthing.config.options.MattermostProgramOptions;
import net.bis5.mattermost.client4.ApiResponse;
import net.bis5.mattermost.client4.MattermostClient;
import net.bis5.mattermost.client4.model.ApiError;
import net.bis5.mattermost.model.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.logging.Level;

public class MattermostMain {
	private MattermostMain() { throw new UnsupportedOperationException(); }

	private static final Logger LOGGER = LoggerFactory.getLogger(MattermostMain.class);

	public static int startMattermost(MattermostProgramOptions options) {
		MattermostClient client = MattermostClient.builder()
				.url(options.getUrl())
				.logLevel(Level.WARNING)
				.ignoreUnknownProperties()
				.build();
		client.setAccessToken(options.getToken());

		while (!Thread.currentThread().isInterrupted()) {

			Post post = new Post();
			post.setChannelId(options.getChannelId());
			post.setCreateAt(System.currentTimeMillis());
			post.setMessage("My message");

			ApiResponse<Post> response = client.createPost(post);
			ApiError error = response.readError();
			if (error != null) {
				LOGGER.info("error: " + error.getDetailedError());
			}
			LOGGER.debug("" + response.getRawResponse());

			try {
				Thread.sleep(5000);
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
				throw new RuntimeException(ex);
			}
		}
		return 0;
	}
}

package me.retrodaredevil.solarthing.actions.chatbot;

import com.google.gson.JsonObject;
import com.slack.api.Slack;
import com.slack.api.socket_mode.SocketModeClient;
import com.slack.api.socket_mode.request.EventsApiEnvelope;
import me.retrodaredevil.action.SimpleAction;
import me.retrodaredevil.solarthing.message.MessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class SlackChatBotAction extends SimpleAction {
	private static final Logger LOGGER = LoggerFactory.getLogger(SlackChatBotAction.class);

	private final String appToken;
	private final Slack slack;
	private final MessageSender messageSender;

	private SocketModeClient client = null;

	public SlackChatBotAction(String appToken, MessageSender messageSender, Slack slack) {
		super(false);
		requireNonNull(this.appToken = appToken);
		requireNonNull(this.messageSender = messageSender);
		requireNonNull(this.slack = slack);
	}

	@Override
	protected void onUpdate() {
		super.onUpdate();
		if (client == null || !client.verifyConnection()) {
			initClient();
		}
	}

	@Override
	protected void onEnd(boolean peacefullyEnded) {
		super.onEnd(peacefullyEnded);
		closeClient();
	}
	private void initClient() {
		final SocketModeClient client;
		try {
			client = slack.socketMode(appToken, SocketModeClient.Backend.JavaWebSocket);
		} catch (IOException e) {
			// slack.socketMode doesn't actually block at all. An IOException is thrown if the URI is invalid, but it doesn't actually try to connect
			throw new RuntimeException(e);
		}
		client.addEventsApiEnvelopeListener(this::handle);
		client.addWebSocketErrorListener(throwable -> {
			LOGGER.error("Got slack connection error", throwable);
		});
		LOGGER.debug("Going to connect");
		try {
			client.connectToNewEndpoint();
			// I'm guessing this blocks, so we may want to move it to a new thread in the future
		} catch (IOException e) {
			// we should handle this exception better later, because it's likely that it could get thrown and crash our program
			try {
				client.close();
			} catch (IOException ioException) {
				e.addSuppressed(ioException);
			}
			throw new RuntimeException(e);
		}
		this.client = client;
		LOGGER.debug("Connect successfully!");
	}
	private void closeClient() {
		try {
			client.close();
			slack.close();
		} catch (IOException e) {
			LOGGER.error("Error while closing client", e);
		} catch (Exception e) {
			throw new RuntimeException("This shouldn't be thrown", e);
		}
	}
	private void handle(EventsApiEnvelope eventsApiEnvelope) {
		if ("events_api".equals(eventsApiEnvelope.getType())) {
			JsonObject payload = eventsApiEnvelope.getPayload().getAsJsonObject();
			JsonObject message = payload.getAsJsonObject("event");
			if ("message".equals(message.get("type").getAsString()) && message.get("subtype") == null) {
				String text = message.get("text").getAsString();
				LOGGER.debug("Got text: " + text + " from message: " + message);
			}
		}
	}
}

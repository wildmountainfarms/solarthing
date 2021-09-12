package me.retrodaredevil.solarthing.actions.chatbot;

import com.google.gson.JsonObject;
import com.slack.api.Slack;
import com.slack.api.socket_mode.SocketModeClient;
import com.slack.api.socket_mode.request.EventsApiEnvelope;
import com.slack.api.socket_mode.response.AckResponse;
import com.slack.api.socket_mode.response.SocketModeResponse;
import me.retrodaredevil.action.SimpleAction;
import me.retrodaredevil.solarthing.chatbot.ChatBotHandler;
import me.retrodaredevil.solarthing.chatbot.Message;
import me.retrodaredevil.solarthing.message.MessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;

import static java.util.Objects.requireNonNull;

public class SlackChatBotAction extends SimpleAction {
	private static final Logger LOGGER = LoggerFactory.getLogger(SlackChatBotAction.class);

	private final String appToken;
	private final Slack slack;
	private final MessageSender messageSender;
	private final ChatBotHandler handler;

	private SocketModeClient client = null;
	/** For some reason we need this because {@link SocketModeClient#verifyConnection()} sometimes returns true after an error happens */
	private volatile boolean needsReinitialize = false;

	public SlackChatBotAction(String appToken, MessageSender messageSender, Slack slack, ChatBotHandler handler) {
		super(false);
		requireNonNull(this.appToken = appToken);
		requireNonNull(this.messageSender = messageSender);
		requireNonNull(this.slack = slack);
		requireNonNull(this.handler = handler);
	}

	@Override
	protected void onUpdate() {
		super.onUpdate();
		if (client == null || !client.verifyConnection() || needsReinitialize) {
			initClient();
		} else {
			LOGGER.debug("We're good and connected!");
		}
	}

	@Override
	protected void onEnd(boolean peacefullyEnded) {
		super.onEnd(peacefullyEnded);
		closeClient();
	}
	private void initClient() {
		LOGGER.debug("Initializing client!");
		SocketModeClient currentClient = this.client;
		if (currentClient != null) {
			LOGGER.debug("Closing current client");
			try {
				currentClient.close();
				LOGGER.debug("Closed without exception");
			} catch (IOException e) {
				LOGGER.error("Got error while current slack socket mode client. (This is probably to be expected)", e);
			}
		}
		final SocketModeClient client;
		try {
			client = slack.socketMode(appToken, SocketModeClient.Backend.JavaWebSocket);
			// This likely does some sort of blocking
		} catch (IOException e) {
			// Relates to https://github.com/wildmountainfarms/solarthing/issues/40
			// If this is an UnknownHostException, then there's likely a temporary failure in DNS resolution, or it just cannot connect
			LOGGER.error("Error doing initial connect to slack", e);
			return;
		}
		client.addEventsApiEnvelopeListener(eventsApiEnvelope -> {
			SocketModeResponse ack = AckResponse.builder().envelopeId(eventsApiEnvelope.getEnvelopeId()).build();
			client.sendSocketModeResponse(ack);
			handle(eventsApiEnvelope);
		});
		client.addWebSocketErrorListener(throwable -> {
			LOGGER.error("Got slack connection error", throwable);
			needsReinitialize = true;
		});
		LOGGER.debug("Going to connect");
		try {
			client.connect();
			// this blocks for ~.5 second. We should eventually put this in another thread
		} catch (IOException e) {
			// we should handle this exception better later, because it's likely that it could get thrown and crash our program
			try {
				client.close();
			} catch (IOException ioException) {
				e.addSuppressed(ioException);
			}
			LOGGER.error("Error connecting", e);
			return;
		}
		this.client = client;
		needsReinitialize = false;
		LOGGER.debug("Connect successfully!");
	}
	private void closeClient() {
		SocketModeClient client = this.client;
		try {
			if (client != null) {
				client.close();
			}
			slack.close();
		} catch (IOException e) {
			LOGGER.error("Error while closing client", e);
		} catch (Exception e) {
			throw new RuntimeException("This shouldn't be thrown", e);
		}
	}
	private void handle(EventsApiEnvelope eventsApiEnvelope) {
		LOGGER.debug("Got a message! type: " + eventsApiEnvelope.getType() + " payload: " + eventsApiEnvelope.getPayload());
		if ("events_api".equals(eventsApiEnvelope.getType())) {
			JsonObject payload = eventsApiEnvelope.getPayload().getAsJsonObject();
			JsonObject message = payload.getAsJsonObject("event");
			if (message.get("bot_id") != null) {
				LOGGER.debug("Got a message from a bot! Ignoring.");
				return;
			}
			if ("message".equals(message.get("type").getAsString()) && message.get("subtype") == null) {
				String text = message.get("text").getAsString();
				Instant timestamp = epochSecondsToInstant(message.get("ts").getAsBigDecimal());

				String userId = message.get("user").getAsString();
				LOGGER.debug("Message raw: " + message);
				LOGGER.debug("Got text: " + text + " from " + userId + " at " + timestamp);
				handler.handleMessage(new Message(text, userId, timestamp), messageSender);
			}
		}
	}
	private static Instant epochSecondsToInstant(BigDecimal timestampBigDecimal) {
		long nanos = timestampBigDecimal.multiply(new BigDecimal(1_000_000)).remainder(new BigDecimal(1000)).longValue() * 1000;
		// convert epoch millis to milliseconds, then add additional nanoseconds
		return Instant.ofEpochMilli(timestampBigDecimal.multiply(new BigDecimal(1000)).longValue())
				.plusNanos(nanos);
	}
}

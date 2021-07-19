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

	public SlackChatBotAction(String appToken, MessageSender messageSender, Slack slack, ChatBotHandler handler) {
		super(false);
		this.handler = handler;
		requireNonNull(this.appToken = appToken);
		requireNonNull(this.messageSender = messageSender);
		requireNonNull(this.slack = slack);
	}

	@Override
	protected void onUpdate() {
		super.onUpdate();
		if (client == null || !client.verifyConnection()) {
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
		final SocketModeClient client;
		try {
			client = slack.socketMode(appToken, SocketModeClient.Backend.JavaWebSocket);
		} catch (IOException e) {
			// slack.socketMode doesn't actually block at all. An IOException is thrown if the URI is invalid, but it doesn't actually try to connect
			throw new RuntimeException(e);
		}
		client.addEventsApiEnvelopeListener(eventsApiEnvelope -> {
			SocketModeResponse ack = AckResponse.builder().envelopeId(eventsApiEnvelope.getEnvelopeId()).build();
			client.sendSocketModeResponse(ack);
			handle(eventsApiEnvelope);
		});
		client.addWebSocketErrorListener(throwable -> {
			LOGGER.error("Got slack connection error", throwable);
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
		LOGGER.debug("Got a message! type: " + eventsApiEnvelope.getType() + " payload: " + eventsApiEnvelope.getPayload());
		if ("events_api".equals(eventsApiEnvelope.getType())) {
			JsonObject payload = eventsApiEnvelope.getPayload().getAsJsonObject();
			JsonObject message = payload.getAsJsonObject("event");
			if ("message".equals(message.get("type").getAsString()) && message.get("subtype") == null) {
				String text = message.get("text").getAsString();
				BigDecimal timestampBigDecimal = message.get("ts").getAsBigDecimal(); // in epoch seconds with microsecond resolution
				long nanos = timestampBigDecimal.multiply(new BigDecimal(1_000_000)).remainder(new BigDecimal(1000)).longValue() * 1000;
				// convert epoch millis to milliseconds, then add additional nanoseconds
				Instant timestamp = Instant.ofEpochMilli(timestampBigDecimal.multiply(new BigDecimal(1000)).longValue())
						.plusNanos(nanos);

				String userId = message.get("user").getAsString();
				LOGGER.debug("Message raw: " + message);
				LOGGER.debug("Got text: " + text + " from " + userId + " at " + timestamp);
				handler.handleMessage(new Message(text, userId, timestamp), messageSender);
			}
		}
	}
}

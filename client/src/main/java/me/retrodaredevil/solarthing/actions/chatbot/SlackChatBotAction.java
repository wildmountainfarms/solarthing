package me.retrodaredevil.solarthing.actions.chatbot;

import com.google.gson.JsonObject;
import com.slack.api.Slack;
import com.slack.api.socket_mode.SocketModeClient;
import com.slack.api.socket_mode.request.EventsApiEnvelope;
import com.slack.api.socket_mode.response.AckResponse;
import com.slack.api.socket_mode.response.SocketModeResponse;
import me.retrodaredevil.action.SimpleAction;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.chatbot.ChatBotHandler;
import me.retrodaredevil.solarthing.chatbot.Message;
import me.retrodaredevil.solarthing.message.MessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

public class SlackChatBotAction extends SimpleAction {
	private static final Logger LOGGER = LoggerFactory.getLogger(SlackChatBotAction.class);

	private final String appToken;
	private final Slack slack;
	private final MessageSender messageSender;
	private final ChatBotHandler handler;

	private final ExecutorService executorService = Executors.newSingleThreadExecutor();

	private volatile SocketModeClient client = null;

	/** This set is synchronized so that in case Slack decides to run on multiple threads we still get thread safety. (I don't know if this is necessary, so this is to be safe)*/
	private final Set<EnvelopeIdAndTimestamp> processedMessages = Collections.synchronizedSet(new HashSet<>());

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
		if (client == null) {
			// It's OK if we submit LOTS of things to run. executorService is single threaded, so only one will run at a time
			executorService.execute(() -> logUncaught(this::initClient));
		} else {
			LOGGER.debug("We're good and connected!");
		}
	}

	@Override
	protected void onEnd(boolean peacefullyEnded) {
		super.onEnd(peacefullyEnded);
		executorService.shutdownNow();
		final boolean success;
		try {
			success = executorService.awaitTermination(200, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			// We don't expect this thread to be interrupted
			throw new RuntimeException(e);
		}
		if (!success) {
			LOGGER.warn("Unable to terminate the running initClient() call!");
		}
		closeClient();
	}
	private static void logUncaught(Runnable runnable) {
		try {
			runnable.run();
		} catch (Throwable e) {
			LOGGER.error("Uncaught exception", e);
		}
	}
	private void initClient() {
		if (this.client != null) {
			return;
		}
		// This is a summary log because there's currently a bug where a long-running Slack chatbot will process messages multiple times
		LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Initializing slack client!");
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
			LOGGER.debug("Got an event. envelopeId: " + eventsApiEnvelope.getEnvelopeId() + " with retry: " + eventsApiEnvelope.getRetryAttempt() + " and reason: " + eventsApiEnvelope.getRetryReason());
			EnvelopeIdAndTimestamp id = new EnvelopeIdAndTimestamp(eventsApiEnvelope.getEnvelopeId(), Instant.now());
			if (processedMessages.contains(id)) {
				// Making this log message a summary message for now to confirm it works. Consider removing the summary marker in the future once this is confirmed to fix the bug.
				LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Envelope ID: " + eventsApiEnvelope.getEnvelopeId() + " has already been processed!");
			} else {
				Instant removeIfBefore = Instant.now().minusSeconds(30 * 60);
				processedMessages.removeIf(envelopeIdAndTimestamp -> envelopeIdAndTimestamp.firstReceiveInstant.isBefore(removeIfBefore));
				processedMessages.add(id);
				handle(eventsApiEnvelope);
			}
		});
		client.addWebSocketErrorListener(throwable -> {
			LOGGER.error("Got slack connection error", throwable);
		});
		client.setAutoReconnectEnabled(true); // as long as we call this, we should never have to call client.connect()
		LOGGER.debug("client set up successfully. Will automatically connect soon.");
		this.client = client;
	}
	private void closeClient() {
		SocketModeClient client = this.client;
		try {
			if (client != null) {
				client.close();
			}
		} catch (IOException e) {
			LOGGER.error("Error while closing client", e);
		} catch (Exception e) {
			throw new RuntimeException("This shouldn't be thrown", e);
		}
		try {
			slack.close();
		} catch (IOException e) {
			LOGGER.error("Error while closing slack", e);
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
				if (timestamp.isBefore(Instant.now().minusSeconds(15 * 60))) {
					LOGGER.debug("We got a message that was from over 15 minutes ago!");
					// Messages that we receive now that were sent more than 15 minutes ago should be ignored to prevent spammy messages.
					//   For instance, the handler that SolarThing uses at the time of writing will send a "Sorry! Try sending 'command' again. It took too long to process".
					//   Before 2024-07-30, many times multiple messages would be sent. As of 2024-07-30, we are keeping track of the envelope IDs
					//   so that we don't process messages twice. However, we delete envelope IDs that we already processed after some time, so
					//   we want to make sure that if a message is from a while ago, we don't process it because we don't know if we already processed it.
					// Additionally, there is not really any reason to believe that this code should ever be hit,
					//   but Slack sure does seem set on making for sure for sure for sure that a message was delivered to us, so this is a safeguard against the Slack gods.
					return;
				}

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

	private static final class EnvelopeIdAndTimestamp {
		private final String envelopeId;
		/** The timestamp of the time an EventsApiEnvelope was received with the id {@link #envelopeId}. This is mere metadata and is not*/
		private final Instant firstReceiveInstant;

		private EnvelopeIdAndTimestamp(String envelopeId, Instant firstReceiveInstant) {
			this.envelopeId = requireNonNull(envelopeId);
			this.firstReceiveInstant = requireNonNull(firstReceiveInstant);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			EnvelopeIdAndTimestamp that = (EnvelopeIdAndTimestamp) o;
			return Objects.equals(envelopeId, that.envelopeId);
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(envelopeId);
		}
	}
}

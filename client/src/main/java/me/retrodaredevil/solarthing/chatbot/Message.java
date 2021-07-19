package me.retrodaredevil.solarthing.chatbot;

import java.time.Instant;

public final class Message {
	private final String text;
	private final String userId;
	private final Instant timestamp;

	public Message(String text, String userId, Instant timestamp) {
		this.text = text;
		this.userId = userId;
		this.timestamp = timestamp;
	}

	public String getText() {
		return text;
	}

	public String getUserId() {
		return userId;
	}

	public Instant getTimestamp() {
		return timestamp;
	}
}

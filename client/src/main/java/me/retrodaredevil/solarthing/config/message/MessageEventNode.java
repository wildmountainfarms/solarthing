package me.retrodaredevil.solarthing.config.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.message.event.MessageEvent;

import java.util.List;

public class MessageEventNode {
	private final MessageEvent messageEvent;
	private final List<String> sendTo;

	@JsonCreator
	public MessageEventNode(
			@JsonProperty(value = "event", required = true) MessageEvent messageEvent,
			@JsonProperty(value = "send_to", required = true) List<String> sendTo
	) {
		this.messageEvent = messageEvent;
		this.sendTo = sendTo;
	}

	public MessageEvent getMessageEvent() {
		return messageEvent;
	}

	public List<String> getSendTo() {
		return sendTo;
	}
}

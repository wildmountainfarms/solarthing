package me.retrodaredevil.solarthing.message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MessageSenderMultiplexer implements MessageSender {
	private final List<MessageSender> messageSenderList;

	public MessageSenderMultiplexer(Collection<? extends MessageSender> messageSenders) {
		this.messageSenderList = Collections.unmodifiableList(new ArrayList<>(messageSenders));
	}

	@Override
	public void sendMessage(String message) {
		for (MessageSender messageSender : messageSenderList) {
			messageSender.sendMessage(message);
		}
	}
}

package me.retrodaredevil.solarthing.message;

import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.List;

@NullMarked
public class MessageSenderMultiplexer implements MessageSender {
	private final List<MessageSender> messageSenderList;

	public MessageSenderMultiplexer(Collection<? extends MessageSender> messageSenders) {
		this.messageSenderList = List.copyOf(messageSenders);
	}

	@Override
	public void sendMessage(String message) {
		for (MessageSender messageSender : messageSenderList) {
			messageSender.sendMessage(message);
		}
	}
}

package me.retrodaredevil.solarthing.chatbot;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.message.MessageSender;

import java.util.Collections;
import java.util.List;

/**
 * Represents something that can (possibly) respond to a message.
 * <p>
 * All implementations must be thread safe.
 */
public interface ChatBotHandler {

	boolean handleMessage(Message message, MessageSender messageSender);

	@NotNull default List<String> getHelpLines(Message helpMessage) {
		return Collections.emptyList();
	}
}

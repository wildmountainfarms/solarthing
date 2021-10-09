package me.retrodaredevil.solarthing.message;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import me.retrodaredevil.solarthing.message.implementations.LogMessageSender;
import me.retrodaredevil.solarthing.message.implementations.SlackMessageSender;

@JsonSubTypes({
		@JsonSubTypes.Type(SlackMessageSender.class),
		@JsonSubTypes.Type(LogMessageSender.class),
})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
public interface MessageSender {
	/**
	 * Sends the given message. Note that this method may block, but it is encouraged that it doesn't block.
	 * <p>
	 * All implementations should be thread safe
	 * @param message The message to send
	 */
	void sendMessage(String message);
}

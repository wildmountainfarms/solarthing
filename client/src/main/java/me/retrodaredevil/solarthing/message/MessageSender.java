package me.retrodaredevil.solarthing.message;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import me.retrodaredevil.solarthing.message.implementations.MattermostMessageSender;

@JsonSubTypes({
		@JsonSubTypes.Type(MattermostMessageSender.class)
})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
public interface MessageSender {
	void sendMessage(String message);
}

package me.retrodaredevil.solarthing.message.implementations;

import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.message.MessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JsonTypeName("log")
public class LogMessageSender implements MessageSender {
	private static final Logger LOGGER = LoggerFactory.getLogger(LogMessageSender.class);
	@Override
	public void sendMessage(String message) {
		LOGGER.info("message: " + message);
	}
}

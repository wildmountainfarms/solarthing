package me.retrodaredevil.solarthing.chatbot;

import me.retrodaredevil.solarthing.message.MessageSender;

public class StaleMessageHandler implements ChatBotHandler {
	@Override
	public boolean handleMessage(Message message, MessageSender messageSender) {
		long timestamp = message.getTimestamp().toEpochMilli();
		long now = System.currentTimeMillis();
		if (now - timestamp > 7000) {
			messageSender.sendMessage("Sorry! Try sending '" + message.getText() + "' again. It took too long to process.");
			return true;
		}

		return false;
	}
}

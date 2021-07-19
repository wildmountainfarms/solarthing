package me.retrodaredevil.solarthing.chatbot;

import me.retrodaredevil.solarthing.message.MessageSender;

public interface ChatBotHandler {

	void handleMessage(Message message, MessageSender messageSender);
}

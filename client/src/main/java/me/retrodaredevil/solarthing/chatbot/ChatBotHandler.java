package me.retrodaredevil.solarthing.chatbot;

import me.retrodaredevil.solarthing.message.MessageSender;

public interface ChatBotHandler {

	boolean handleMessage(Message message, MessageSender messageSender);
}

package me.retrodaredevil.solarthing.chatbot;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.message.MessageSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ChatBotHandlerMultiplexer implements ChatBotHandler {
	private final List<ChatBotHandler> chatBotHandlerList;

	public ChatBotHandlerMultiplexer(List<? extends ChatBotHandler> chatBotHandlerList) {
		this.chatBotHandlerList = Collections.unmodifiableList(new ArrayList<>(chatBotHandlerList));
	}

	@Override
	public boolean handleMessage(Message message, MessageSender messageSender) {
		for (ChatBotHandler chatBotHandler : chatBotHandlerList) {
			if (chatBotHandler.handleMessage(message, messageSender)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public @NotNull List<String> getHelpLines(Message helpMessage) {
		return chatBotHandlerList.stream()
				.flatMap(chatBotHandler -> chatBotHandler.getHelpLines(helpMessage).stream())
				.collect(Collectors.toList());
	}
}

package me.retrodaredevil.solarthing.chatbot;

import me.retrodaredevil.solarthing.message.MessageSender;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.stream.Collectors;

@NullMarked
public class ChatBotHandlerMultiplexer implements ChatBotHandler {
	private final List<ChatBotHandler> chatBotHandlerList;

	public ChatBotHandlerMultiplexer(List<? extends ChatBotHandler> chatBotHandlerList) {
		this.chatBotHandlerList = List.copyOf(chatBotHandlerList);
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
	public List<String> getHelpLines(Message helpMessage) {
		return chatBotHandlerList.stream()
				.flatMap(chatBotHandler -> chatBotHandler.getHelpLines(helpMessage).stream())
				.collect(Collectors.toList());
	}
}

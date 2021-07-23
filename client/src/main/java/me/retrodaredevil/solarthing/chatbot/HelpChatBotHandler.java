package me.retrodaredevil.solarthing.chatbot;

import me.retrodaredevil.solarthing.message.MessageSender;

public class HelpChatBotHandler implements ChatBotHandler {
	private final ChatBotHandler chatBotHandler;

	public HelpChatBotHandler(ChatBotHandler chatBotHandler) {
		this.chatBotHandler = chatBotHandler;
	}

	@Override
	public boolean handleMessage(Message message, MessageSender messageSender) {
		if (ChatBotUtil.isSimilar(message.getText(), "help")
				|| ChatBotUtil.isSimilar(message.getText(), "list")
				|| ChatBotUtil.isSimilar(message.getText(), "list commands")
				|| ChatBotUtil.isSimilar(message.getText(), "commands")) {
			messageSender.sendMessage(String.join("\n", chatBotHandler.getHelpLines(message)));
			return true;
		}

		return chatBotHandler.handleMessage(message, messageSender);
	}
}

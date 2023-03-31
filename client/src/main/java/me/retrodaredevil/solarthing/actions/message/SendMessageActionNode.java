package me.retrodaredevil.solarthing.actions.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.solarthing.message.MessageSender;
import me.retrodaredevil.solarthing.config.ConfigUtil;

import java.nio.file.Path;

import static java.util.Objects.requireNonNull;

/**
 * An action node that is designed to send a message directly to a {@link MessageSender}
 */
@JsonTypeName("sendmessage")
public class SendMessageActionNode implements ActionNode {

	private final MessageSender messageSender;
	private final String message;

	public SendMessageActionNode(MessageSender messageSender, String message) {
		requireNonNull(this.messageSender = messageSender);
		requireNonNull(this.message = message);
	}

	@JsonCreator
	public static SendMessageActionNode createFromFileAndMessage(
			@JsonProperty("send_to") Path messageSenderFile,
			@JsonProperty("message") String message) {
		requireNonNull(messageSenderFile, "send_to must be present and cannot be blank!");
		MessageSender messageSender = ConfigUtil.readConfig(messageSenderFile, MessageSender.class);
		return new SendMessageActionNode(messageSender, message);
	}

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		return Actions.createRunOnce(() -> messageSender.sendMessage(message));
	}
}

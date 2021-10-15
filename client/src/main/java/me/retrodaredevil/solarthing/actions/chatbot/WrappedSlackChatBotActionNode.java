package me.retrodaredevil.solarthing.actions.chatbot;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.action.node.convenient.SingleActionNode;
import me.retrodaredevil.action.node.environment.ActionEnvironment;

import java.io.File;
import java.util.List;
import java.util.Map;

@JsonTypeName("chatbot_slack")
public class WrappedSlackChatBotActionNode implements ActionNode {

	private final SingleActionNode actionNode;

	public WrappedSlackChatBotActionNode(
			@JsonProperty(value = "app_token", required = true) String appToken,
			@JsonProperty(value = "token", required = true) String authToken,
			@JsonProperty(value = "channel_id", required = true) String channelId,
			@JsonProperty(value = "permissions", required = true) Map<String, List<String>> permissionMap,
			@JsonProperty(value = "sender", required = true) String sender,
			@JsonProperty(value = "key_directory", required = true) File keyDirectory
	) {
		// Wrap the RawActionNode in a SingleActionNode so that only one is active at a time -- the first one created and executed will be the one that is used
		actionNode = SingleActionNode.create(new SlackChatBotActionNode(appToken, authToken, channelId, permissionMap, sender, keyDirectory));
	}

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		return actionNode.createAction(actionEnvironment);
	}
}

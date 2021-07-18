package me.retrodaredevil.solarthing.actions.chatbot;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.slack.api.Slack;
import com.slack.api.SlackConfig;
import com.slack.api.util.http.SlackHttpClient;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.solarthing.actions.ActionNode;
import me.retrodaredevil.solarthing.actions.environment.ActionEnvironment;
import me.retrodaredevil.solarthing.message.implementations.SlackMessageSender;
import okhttp3.OkHttpClient;

import java.time.Duration;

@JsonTypeName("chatbot_slack")
public class SlackChatBotActionNode implements ActionNode {

	private final Action action;

	public SlackChatBotActionNode(
			@JsonProperty(value = "app_token", required = true) String appToken,
			@JsonProperty(value = "token", required = true) String authToken,
			@JsonProperty(value = "channel_id", required = true) String channelId) {
		Slack slack = Slack.getInstance(new SlackConfig(), new SlackHttpClient(new OkHttpClient.Builder()
				.callTimeout(Duration.ofSeconds(10))
				.connectTimeout(Duration.ofSeconds(4))
				.build()));
		action = new SlackChatBotAction(appToken, new SlackMessageSender(authToken, channelId, slack), slack);
	}

	// This is designed to be handled by the automation program.
	// The automation program creates a new action each iteration,
	//   so we update a single action each iteration instead of creating a new
	//   one a bunch of times.

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		return Actions.createRunOnce(action::update);
	}
}

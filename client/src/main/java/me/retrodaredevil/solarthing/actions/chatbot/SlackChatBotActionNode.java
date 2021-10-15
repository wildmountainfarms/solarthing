package me.retrodaredevil.solarthing.actions.chatbot;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.slack.api.Slack;
import com.slack.api.SlackConfig;
import com.slack.api.util.http.SlackHttpClient;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.action.node.convenient.SingleActionNode;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.solarthing.AlterPacketsProvider;
import me.retrodaredevil.solarthing.FragmentedPacketGroupProvider;
import me.retrodaredevil.solarthing.actions.command.CommandManager;
import me.retrodaredevil.solarthing.actions.environment.AlterPacketsEnvironment;
import me.retrodaredevil.solarthing.actions.environment.LatestFragmentedPacketGroupEnvironment;
import me.retrodaredevil.solarthing.chatbot.*;
import me.retrodaredevil.solarthing.message.implementations.SlackMessageSender;
import okhttp3.OkHttpClient;

import java.io.File;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@JsonTypeName("chatbot_slack")
public class SlackChatBotActionNode implements ActionNode {

	private final SingleActionNode actionNode;

	/*
	The app level token should have connections:write permission
	Then add the app to the channel you want it to listen to
	You also need to enable "Socket Mode" for your app
	Then subscribe to events: message.channels
	Then make sure to reinstall your app
	 */

	// useful doc: https://slack.dev/java-slack-sdk/guides/socket-mode

	public SlackChatBotActionNode(
			@JsonProperty(value = "app_token", required = true) String appToken,
			@JsonProperty(value = "token", required = true) String authToken,
			@JsonProperty(value = "channel_id", required = true) String channelId,
			@JsonProperty(value = "permissions", required = true) Map<String, List<String>> permissionMap,
			@JsonProperty(value = "sender", required = true) String sender,
			@JsonProperty(value = "key_directory", required = true) File keyDirectory
	) {
		// Wrap the RawActionNode in a SingleActionNode so that only one is active at a time -- the first one created and executed will be the one that is used
		actionNode = SingleActionNode.create(new RawActionNode(appToken, authToken, channelId, permissionMap, sender, keyDirectory));
	}

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		return actionNode.createAction(actionEnvironment);
	}

	public static class RawActionNode implements ActionNode {
		private final String appToken;
		private final String authToken;
		private final String channelId;
		private final Map<String, List<String>> permissionMap;
		private final String sender;
		private final File keyDirectory;

		public RawActionNode(
				String appToken,
				String authToken,
				String channelId,
				Map<String, List<String>> permissionMap,
				String sender,
				File keyDirectory
		) {
			this.appToken = appToken;
			this.authToken = authToken;
			this.channelId = channelId;
			this.permissionMap = permissionMap;
			this.sender = sender;
			this.keyDirectory = keyDirectory;
		}

		@Override
		public Action createAction(ActionEnvironment actionEnvironment) {
			LatestFragmentedPacketGroupEnvironment latestPacketGroupEnvironment = actionEnvironment.getInjectEnvironment().get(LatestFragmentedPacketGroupEnvironment.class);
			AlterPacketsEnvironment alterPacketsEnvironment = actionEnvironment.getInjectEnvironment().get(AlterPacketsEnvironment.class);

			FragmentedPacketGroupProvider packetGroupProvider = latestPacketGroupEnvironment.getFragmentedPacketGroupProvider();
			AlterPacketsProvider alterPacketsProvider = alterPacketsEnvironment.getAlterPacketsProvider();


			Slack slack = Slack.getInstance(new SlackConfig(), new SlackHttpClient(new OkHttpClient.Builder()
					.callTimeout(Duration.ofSeconds(10))
					.connectTimeout(Duration.ofSeconds(4))
					.build()));

			ChatBotCommandHelper commandHelper = new ChatBotCommandHelper(permissionMap, packetGroupProvider, new CommandManager(keyDirectory, sender));

			return new SlackChatBotAction(
					appToken,
					new SlackMessageSender(authToken, channelId, slack),
					slack,
					new HelpChatBotHandler(
							new ChatBotHandlerMultiplexer(Arrays.asList(
									new StaleMessageHandler(), // note: this isn't applied to "help" commands
									new ScheduleCommandChatBotHandler(commandHelper, actionEnvironment.getInjectEnvironment()),
									new CommandChatBotHandler(commandHelper, actionEnvironment.getInjectEnvironment()),
									new StatusChatBotHandler(packetGroupProvider, alterPacketsProvider),
									(message, messageSender) -> {
										messageSender.sendMessage("Unknown command!");
										return true;
									}
							))
					)
			);
		}
	}
}

package me.retrodaredevil.solarthing.actions.chatbot;

import com.slack.api.Slack;
import com.slack.api.SlackConfig;
import com.slack.api.util.http.SlackHttpClient;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.solarthing.AlterPacketsProvider;
import me.retrodaredevil.solarthing.FragmentedPacketGroupProvider;
import me.retrodaredevil.solarthing.commands.util.CommandManager;
import me.retrodaredevil.solarthing.actions.environment.*;
import me.retrodaredevil.solarthing.chatbot.*;
import me.retrodaredevil.solarthing.database.SolarThingDatabase;
import me.retrodaredevil.solarthing.message.implementations.SlackMessageSender;
import okhttp3.OkHttpClient;

import java.io.File;
import java.time.Duration;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * The action node for creating a slack chat bot action.
 * <p>
 * Note: Only one of these should be in use at a time in the automation program. You can use {@link WrappedSlackChatBotActionNode} to achieve this
 */
public class SlackChatBotActionNode implements ActionNode {

	/*
	The app level token should have connections:write permission
	Then add the app to the channel you want it to listen to
	You also need to enable "Socket Mode" for your app
	Then subscribe to events: message.channels
	Then make sure to reinstall your app
	 */

	// useful doc: https://slack.dev/java-slack-sdk/guides/socket-mode

	private final String appToken;
	private final String authToken;
	private final String channelId;
	private final Map<String, List<String>> permissionMap;
	private final String sender;
	private final File keyDirectory;

	public SlackChatBotActionNode(
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
		SolarThingDatabaseEnvironment solarThingDatabaseEnvironment = actionEnvironment.getInjectEnvironment().get(SolarThingDatabaseEnvironment.class);
		SourceIdEnvironment sourceIdEnvironment = actionEnvironment.getInjectEnvironment().get(SourceIdEnvironment.class);
		TimeZoneEnvironment timeZoneEnvironment = actionEnvironment.getInjectEnvironment().get(TimeZoneEnvironment.class);

		FragmentedPacketGroupProvider packetGroupProvider = latestPacketGroupEnvironment.getFragmentedPacketGroupProvider();
		AlterPacketsProvider alterPacketsProvider = alterPacketsEnvironment.getAlterPacketsProvider();
		SolarThingDatabase database = solarThingDatabaseEnvironment.getSolarThingDatabase();
		String sourceId = sourceIdEnvironment.getSourceId();
		ZoneId zoneId = timeZoneEnvironment.getZoneId();


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
								new ScheduleCommandChatBotHandler(commandHelper, database, sourceId, zoneId),
								new CancelCommandChatBotHandler(commandHelper, database, sourceId, zoneId, alterPacketsProvider),
								new FlagCommandChatBotHandler(commandHelper, database, sourceId, zoneId, alterPacketsProvider),
								new CommandChatBotHandler(commandHelper, database, sourceId, zoneId),
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

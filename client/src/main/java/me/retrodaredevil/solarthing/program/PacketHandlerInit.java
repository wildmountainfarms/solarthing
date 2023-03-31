package me.retrodaredevil.solarthing.program;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.action.ActionMultiplexer;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.action.node.environment.InjectEnvironment;
import me.retrodaredevil.action.node.environment.NanoTimeProviderEnvironment;
import me.retrodaredevil.action.node.environment.VariableEnvironment;
import me.retrodaredevil.action.node.util.NanoTimeProvider;
import me.retrodaredevil.couchdb.CouchDbUtil;
import me.retrodaredevil.couchdbjava.CouchDbInstance;
import me.retrodaredevil.solarthing.PacketGroupReceiver;
import me.retrodaredevil.solarthing.PacketGroupReceiverMultiplexer;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.actions.command.EnvironmentUpdater;
import me.retrodaredevil.solarthing.actions.environment.EventReceiverEnvironment;
import me.retrodaredevil.solarthing.actions.environment.ExecutionReasonEnvironment;
import me.retrodaredevil.solarthing.actions.environment.LatestPacketGroupEnvironment;
import me.retrodaredevil.solarthing.actions.environment.SourceIdEnvironment;
import me.retrodaredevil.solarthing.actions.environment.TimeZoneEnvironment;
import me.retrodaredevil.solarthing.annotations.UtilityClass;
import me.retrodaredevil.solarthing.config.ConfigUtil;
import me.retrodaredevil.solarthing.config.databases.IndividualSettings;
import me.retrodaredevil.solarthing.config.databases.implementations.CouchDbDatabaseSettings;
import me.retrodaredevil.solarthing.config.databases.implementations.InfluxDb2DatabaseSettings;
import me.retrodaredevil.solarthing.config.databases.implementations.InfluxDbDatabaseSettings;
import me.retrodaredevil.solarthing.config.databases.implementations.LatestFileDatabaseSettings;
import me.retrodaredevil.solarthing.config.databases.implementations.MqttDatabaseSettings;
import me.retrodaredevil.solarthing.config.databases.implementations.PostDatabaseSettings;
import me.retrodaredevil.solarthing.config.options.ActionsOption;
import me.retrodaredevil.solarthing.config.options.CommandOption;
import me.retrodaredevil.solarthing.config.options.PacketHandlingOption;
import me.retrodaredevil.solarthing.couchdb.CouchDbPacketSaver;
import me.retrodaredevil.solarthing.influxdb.ConstantNameGetter;
import me.retrodaredevil.solarthing.influxdb.influxdb1.ConstantMeasurementPacketPointCreator;
import me.retrodaredevil.solarthing.influxdb.influxdb1.DocumentedMeasurementPacketPointCreator;
import me.retrodaredevil.solarthing.influxdb.influxdb1.InfluxDbPacketSaver;
import me.retrodaredevil.solarthing.influxdb.infuxdb2.DocumentedMeasurementPacketPoint2Creator;
import me.retrodaredevil.solarthing.influxdb.infuxdb2.InfluxDb2PacketSaver;
import me.retrodaredevil.solarthing.influxdb.retention.ConstantRetentionPolicyGetter;
import me.retrodaredevil.solarthing.influxdb.retention.FrequentRetentionPolicyGetter;
import me.retrodaredevil.solarthing.mqtt.MqttPacketSaver;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.handling.*;
import me.retrodaredevil.solarthing.packets.handling.implementations.FileWritePacketHandler;
import me.retrodaredevil.solarthing.packets.handling.implementations.JacksonStringPacketHandler;
import me.retrodaredevil.solarthing.packets.handling.implementations.PostPacketHandler;
import me.retrodaredevil.solarthing.program.receiver.ActionNodeDataReceiver;
import me.retrodaredevil.solarthing.program.receiver.RequestHeartbeatReceiver;
import me.retrodaredevil.solarthing.reason.ExecutionReason;
import me.retrodaredevil.solarthing.reason.OpenSourceExecutionReason;
import me.retrodaredevil.solarthing.reason.PacketCollectionExecutionReason;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import me.retrodaredevil.solarthing.util.frequency.FrequentHandler;
import okhttp3.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

@UtilityClass
public class PacketHandlerInit {
	private PacketHandlerInit(){ throw new UnsupportedOperationException(); }

	private static final Logger LOGGER = LoggerFactory.getLogger(PacketHandlerInit.class);
	private static final ObjectMapper MAPPER = JacksonUtil.defaultMapper();

	public static PacketHandlerBundle getPacketHandlerBundle(List<DatabaseConfig> configs, String uniqueStatusName, String uniqueEventName, String sourceId, int fragmentId){
		List<PacketHandler> statusPacketHandlers = new ArrayList<>();
		List<PacketHandler> eventPacketHandlers = new ArrayList<>();
		for(DatabaseConfig config : configs) {
			IndividualSettings statusIndividualSettings = config.getIndividualSettingsOrDefault(Constants.DATABASE_UPLOAD_ID, null);
			FrequencySettings statusFrequencySettings = statusIndividualSettings != null ? statusIndividualSettings.getFrequencySettings() : FrequencySettings.NORMAL_SETTINGS;

			if (CouchDbDatabaseSettings.TYPE.equals(config.getType())) {
				CouchDbDatabaseSettings settings = (CouchDbDatabaseSettings) config.getSettings();
				CouchDbInstance instance = CouchDbUtil.createInstance(settings.getCouchProperties(), settings.getOkHttpProperties());
				statusPacketHandlers.add(new ThrottleFactorPacketHandler(
						new AsyncPacketHandlerWrapper(new PrintPacketHandleExceptionWrapper(new CouchDbPacketSaver(instance.getDatabase(uniqueStatusName), false))),
						statusFrequencySettings
				));
				eventPacketHandlers.add(new AsyncRetryingPacketHandler(new CouchDbPacketSaver(instance.getDatabase(uniqueEventName), true)));
			} else if(InfluxDbDatabaseSettings.TYPE.equals(config.getType())) {
				LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "You are using InfluxDB 1.X! It is recommended that you switch to 2.0, but is not required.");
				InfluxDbDatabaseSettings settings = (InfluxDbDatabaseSettings) config.getSettings();
				String databaseName = settings.getDatabaseName();
				String measurementName = settings.getMeasurementName();
				statusPacketHandlers.add(new ThrottleFactorPacketHandler(
						new AsyncPacketHandlerWrapper(new PrintPacketHandleExceptionWrapper(new InfluxDbPacketSaver(
								settings.getInfluxProperties(),
								settings.getOkHttpProperties(),
								new ConstantNameGetter(databaseName != null ? databaseName : uniqueStatusName),
								measurementName != null
										? new ConstantMeasurementPacketPointCreator(measurementName)
										: (databaseName != null
												? new ConstantMeasurementPacketPointCreator(uniqueStatusName)
												: DocumentedMeasurementPacketPointCreator.INSTANCE
										),
								new FrequentRetentionPolicyGetter(new FrequentHandler<>(settings.getFrequentStatusRetentionPolicyList()))
						))),
						statusFrequencySettings
				));
				eventPacketHandlers.add(new AsyncRetryingPacketHandler(new InfluxDbPacketSaver(
						settings.getInfluxProperties(),
						settings.getOkHttpProperties(),
						new ConstantNameGetter(databaseName != null ? databaseName : uniqueEventName),
						measurementName != null
								? new ConstantMeasurementPacketPointCreator(measurementName)
								: (databaseName != null
										? new ConstantMeasurementPacketPointCreator(uniqueEventName)
										: DocumentedMeasurementPacketPointCreator.INSTANCE
								),
						new ConstantRetentionPolicyGetter(settings.getEventRetentionPolicy())
				)));
			} else if(InfluxDb2DatabaseSettings.TYPE.equals(config.getType())) {
				InfluxDb2DatabaseSettings settings = (InfluxDb2DatabaseSettings) config.getSettings();
				statusPacketHandlers.add(new ThrottleFactorPacketHandler(
						new AsyncPacketHandlerWrapper(new PrintPacketHandleExceptionWrapper(new InfluxDb2PacketSaver(
								settings.getInfluxDbProperties(),
								settings.getOkHttpProperties(),
								new ConstantNameGetter(uniqueStatusName),
								DocumentedMeasurementPacketPoint2Creator.INSTANCE
						))),
						statusFrequencySettings
				));
				eventPacketHandlers.add(new AsyncRetryingPacketHandler(new InfluxDb2PacketSaver(
						settings.getInfluxDbProperties(),
						settings.getOkHttpProperties(),
						new ConstantNameGetter(uniqueEventName),
						DocumentedMeasurementPacketPoint2Creator.INSTANCE
				)));
			} else if (LatestFileDatabaseSettings.TYPE.equals(config.getType())){
				LatestFileDatabaseSettings settings = (LatestFileDatabaseSettings) config.getSettings();
				LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Adding latest file 'database'. This currently only saves 'status' packets");
				statusPacketHandlers.add(new ThrottleFactorPacketHandler(
						new FileWritePacketHandler(settings.getFile(), new JacksonStringPacketHandler(MAPPER), false),
						statusFrequencySettings
				));
			} else if (PostDatabaseSettings.TYPE.equals(config.getType())) {
				PostDatabaseSettings settings = (PostDatabaseSettings) config.getSettings();

				statusPacketHandlers.add(new ThrottleFactorPacketHandler(
						new AsyncPacketHandlerWrapper(new PostPacketHandler(settings.getUrl(), new JacksonStringPacketHandler(MAPPER), MediaType.get("application/json"))),
						statusFrequencySettings
				));
			} else if (MqttDatabaseSettings.TYPE.equals(config.getType())) {
				MqttDatabaseSettings settings = (MqttDatabaseSettings) config.getSettings();

				String client = settings.getClientId();
				if (client == null) {
					client = "solarthing-" + sourceId + "-" + fragmentId;
				}

				statusPacketHandlers.add(new ThrottleFactorPacketHandler(
						new AsyncPacketHandlerWrapper(new MqttPacketSaver(settings.getBroker(), client, settings.getUsername(), settings.getPassword(), settings.getTopicFormat(), settings.isRetain(), sourceId, fragmentId)),
						statusFrequencySettings
				));
			}
		}
		return new PacketHandlerBundle(statusPacketHandlers, eventPacketHandlers);
	}

	public static <T extends PacketHandlingOption & CommandOption & ActionsOption> Result initHandlers(T options, Supplier<? extends EnvironmentUpdater> environmentUpdaterSupplier, Collection<? extends PacketHandler> additionalPacketHandlers) throws IOException {
		List<DatabaseConfig> databaseConfigs = ConfigUtil.getDatabaseConfigs(options);
		PacketHandlerBundle packetHandlerBundle = PacketHandlerInit.getPacketHandlerBundle(databaseConfigs, SolarThingConstants.STATUS_DATABASE, SolarThingConstants.EVENT_DATABASE, options.getSourceId(), options.getFragmentId());
		List<PacketHandler> statusPacketHandlers = new ArrayList<>();

		final Runnable updateCommandActions;
		if (options.hasCommands()) {
			LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Command are enabled!");
			LatestPacketHandler latestPacketHandler = new LatestPacketHandler(); // this is used to determine the state of the system when a command is requested
			statusPacketHandlers.add(latestPacketHandler);

			Map<String, ActionNode> actionNodeMap = ActionUtil.createCommandNameToActionNodeMap(options);
			ActionNodeDataReceiver commandReceiver = new ActionNodeDataReceiver(
					actionNodeMap,
					(executionReason, injectEnvironmentBuilder) -> {
						if (!(executionReason instanceof OpenSourceExecutionReason)) {
							LOGGER.warn("We usually expect the execution reason to be an OpenSourceExecutionReason. If the program logic has changed, remove this log message!");
						}
						injectEnvironmentBuilder
								.add(new NanoTimeProviderEnvironment(NanoTimeProvider.SYSTEM_NANO_TIME))
								.add(new TimeZoneEnvironment(options.getZoneId()))
								.add(new LatestPacketGroupEnvironment(latestPacketHandler::getLatestPacketCollection))
								.add(new ExecutionReasonEnvironment(executionReason))
								.add(new EventReceiverEnvironment(PacketListReceiverHandlerBundle.createEventPacketListReceiverHandler(SolarMain.getSourceAndFragmentUpdater(options), options.getZoneId(), packetHandlerBundle)))
						;
						EnvironmentUpdater environmentUpdater = environmentUpdaterSupplier.get();
						if (environmentUpdater == null) {
							throw new NullPointerException("The EnvironmentUpdater supplier gave a null value! (Fatal)");
						}
						environmentUpdater.updateInjectEnvironment(executionReason, injectEnvironmentBuilder);
					}
			);
			PacketGroupReceiver mainPacketGroupReceiver = new PacketGroupReceiverMultiplexer(Arrays.asList(
					commandReceiver,
					new RequestHeartbeatReceiver(PacketListReceiverHandlerBundle.createEventPacketListReceiverHandler(SolarMain.getSourceAndFragmentUpdater(options), options.getZoneId(), packetHandlerBundle))
			));

			statusPacketHandlers.add((packetCollection) -> commandReceiver.getActionUpdater().update());

			List<PacketHandler> commandPacketHandlers = CommandUtil.getCommandRequesterHandlerList(databaseConfigs, mainPacketGroupReceiver, options);
			statusPacketHandlers.add(new PacketHandlerMultiplexer(commandPacketHandlers));
			updateCommandActions = () -> commandReceiver.getActionUpdater().update();
		} else {
			LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Commands are disabled");
			updateCommandActions = () -> {};
		}
		statusPacketHandlers.add(createActionExecutorPacketHandler(options, environmentUpdaterSupplier));
		statusPacketHandlers.addAll(additionalPacketHandlers);
		statusPacketHandlers.addAll(packetHandlerBundle.getStatusPacketHandlers());

		PacketListReceiverHandlerBundle bundle = PacketListReceiverHandlerBundle.createFrom(options, packetHandlerBundle, statusPacketHandlers);

		return new Result(bundle, updateCommandActions);
	}
	private static <T extends ActionsOption & PacketHandlingOption> PacketHandler createActionExecutorPacketHandler(T options, Supplier<? extends EnvironmentUpdater> environmentUpdaterSupplier) throws IOException {
		requireNonNull(options);
		requireNonNull(environmentUpdaterSupplier);
		List<ActionNodeEntry> originalActionNodeEntries = ActionUtil.createActionNodeEntries(options);

		VariableEnvironment globalVariableEnvironment = new VariableEnvironment();

		ActionMultiplexer multiplexer = new Actions.ActionMultiplexerBuilder().build();
		List<ActionNodeEntry> actionNodeEntries = new ArrayList<>(originalActionNodeEntries); // entries may be removed from this list

		PacketCollection[] packetCollectionReference = new PacketCollection[] { null };
		LatestPacketGroupEnvironment latestPacketGroupEnvironment = new LatestPacketGroupEnvironment(() -> requireNonNull(packetCollectionReference[0], "Using latestPacketGroupEnvironment before initializing packet collection!"));

		return packetCollection -> {
			packetCollectionReference[0] = packetCollection;

			EnvironmentUpdater environmentUpdater = environmentUpdaterSupplier.get();
			ExecutionReason executionReason = new PacketCollectionExecutionReason(packetCollection.getDateMillis(), packetCollection.getDbId());
			InjectEnvironment.Builder injectEnvironmentBuilder = new InjectEnvironment.Builder()
					.add(new ExecutionReasonEnvironment(executionReason))
					.add(new NanoTimeProviderEnvironment(NanoTimeProvider.SYSTEM_NANO_TIME))
					.add(new SourceIdEnvironment(options.getSourceId()))
					.add(new TimeZoneEnvironment(options.getZoneId()))
					.add(latestPacketGroupEnvironment)
					;
			environmentUpdater.updateInjectEnvironment(executionReason, injectEnvironmentBuilder);
			InjectEnvironment injectEnvironment = injectEnvironmentBuilder.build();

			for (Iterator<ActionNodeEntry> iterator = actionNodeEntries.iterator(); iterator.hasNext(); ) {
				ActionNodeEntry actionNodeEntry = iterator.next();
				multiplexer.add(actionNodeEntry.getActionNode().createAction(new ActionEnvironment(globalVariableEnvironment, injectEnvironment)));
				if (actionNodeEntry.isRunOnce()) {
					iterator.remove();
				}
			}
			multiplexer.update();
		};
	}

	public static class Result {
		private final PacketListReceiverHandlerBundle bundle;
		private final Runnable updateCommandActions;

		public Result(PacketListReceiverHandlerBundle bundle, Runnable updateCommandActions) {
			this.bundle = bundle;
			this.updateCommandActions = updateCommandActions;
		}

		public PacketListReceiverHandlerBundle getBundle() {
			return bundle;
		}

		public Runnable getUpdateCommandActions() {
			return updateCommandActions;
		}
	}

}

package me.retrodaredevil.solarthing.program;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.couchdb.CouchProperties;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.annotations.UtilityClass;
import me.retrodaredevil.solarthing.config.databases.IndividualSettings;
import me.retrodaredevil.solarthing.config.databases.implementations.*;
import me.retrodaredevil.solarthing.couchdb.CouchDbPacketSaver;
import me.retrodaredevil.solarthing.influxdb.ConstantNameGetter;
import me.retrodaredevil.solarthing.influxdb.influxdb1.ConstantMeasurementPacketPointCreator;
import me.retrodaredevil.solarthing.influxdb.influxdb1.DocumentedMeasurementPacketPointCreator;
import me.retrodaredevil.solarthing.influxdb.influxdb1.InfluxDbPacketSaver;
import me.retrodaredevil.solarthing.influxdb.infuxdb2.ConstantMeasurementPacketPoint2Creator;
import me.retrodaredevil.solarthing.influxdb.infuxdb2.DocumentedMeasurementPacketPoint2Creator;
import me.retrodaredevil.solarthing.influxdb.infuxdb2.InfluxDb2PacketSaver;
import me.retrodaredevil.solarthing.influxdb.retention.ConstantRetentionPolicyGetter;
import me.retrodaredevil.solarthing.influxdb.retention.FrequentRetentionPolicyGetter;
import me.retrodaredevil.solarthing.mqtt.MqttPacketSaver;
import me.retrodaredevil.solarthing.packets.handling.*;
import me.retrodaredevil.solarthing.packets.handling.implementations.FileWritePacketHandler;
import me.retrodaredevil.solarthing.packets.handling.implementations.JacksonStringPacketHandler;
import me.retrodaredevil.solarthing.packets.handling.implementations.PostPacketHandler;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import me.retrodaredevil.solarthing.util.frequency.FrequentHandler;
import okhttp3.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

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
			IndividualSettings eventIndividualSettings = config.getIndividualSettingsOrDefault(Constants.DATABASE_UPLOAD_EVENT_ID, null);
//			FrequencySettings eventFrequencySettings = eventIndividualSettings != null ? eventIndividualSettings.getFrequencySettings() : FrequencySettings.NORMAL_SETTINGS;
			if(eventIndividualSettings != null){
				LOGGER.warn(SolarThingConstants.SUMMARY_MARKER, "Individual settings were declared for " + Constants.DATABASE_UPLOAD_EVENT_ID + "! These will not be used in this version! config=" + config);
			}

			if (CouchDbDatabaseSettings.TYPE.equals(config.getType())) {
				CouchDbDatabaseSettings settings = (CouchDbDatabaseSettings) config.getSettings();
				CouchProperties couchProperties = settings.getCouchProperties();
				statusPacketHandlers.add(new ThrottleFactorPacketHandler(
						new PrintPacketHandleExceptionWrapper(new CouchDbPacketSaver(couchProperties, uniqueStatusName)),
						statusFrequencySettings,
						true
				));
				// TODO We should use Constants.DATABASE_UPLOAD_EVENT_ID and its FrequencySettings to stop this from doing stuff too frequently.
				// The reason we aren't going to use a ThrottleFactorPacketHandler is all "event" packets are important. We do not want to
				// miss adding a single event packet to a database
				eventPacketHandlers.add(new RetryFailedPacketHandler(new CouchDbPacketSaver(couchProperties, uniqueEventName), 7));
			} else if(InfluxDbDatabaseSettings.TYPE.equals(config.getType())) {
				LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "You are using InfluxDB 1.X! It is recommended that you switch to 2.0, but is not required.");
				InfluxDbDatabaseSettings settings = (InfluxDbDatabaseSettings) config.getSettings();
				String databaseName = settings.getDatabaseName();
				String measurementName = settings.getMeasurementName();
				statusPacketHandlers.add(new ThrottleFactorPacketHandler(
						new InfluxDbPacketSaver(
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
						),
						statusFrequencySettings,
						true
				));
				eventPacketHandlers.add(new RetryFailedPacketHandler(new InfluxDbPacketSaver(
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
				), 5));
			} else if(InfluxDb2DatabaseSettings.TYPE.equals(config.getType())) {
				InfluxDb2DatabaseSettings settings = (InfluxDb2DatabaseSettings) config.getSettings();
				statusPacketHandlers.add(new ThrottleFactorPacketHandler(
						new InfluxDb2PacketSaver(
								settings.getInfluxDbProperties(),
								settings.getOkHttpProperties(),
								new ConstantNameGetter(uniqueStatusName),
								DocumentedMeasurementPacketPoint2Creator.INSTANCE
						),
						statusFrequencySettings,
						true
				));
				eventPacketHandlers.add(new RetryFailedPacketHandler(new InfluxDb2PacketSaver(
						settings.getInfluxDbProperties(),
						settings.getOkHttpProperties(),
						new ConstantNameGetter(uniqueEventName),
						DocumentedMeasurementPacketPoint2Creator.INSTANCE
				), 5));
			} else if (LatestFileDatabaseSettings.TYPE.equals(config.getType())){
				LatestFileDatabaseSettings settings = (LatestFileDatabaseSettings) config.getSettings();
				LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Adding latest file 'database'. This currently only saves 'status' packets");
				statusPacketHandlers.add(new ThrottleFactorPacketHandler(
						new FileWritePacketHandler(settings.getFile(), new JacksonStringPacketHandler(MAPPER), false),
						statusFrequencySettings,
						false
				));
			} else if (PostDatabaseSettings.TYPE.equals(config.getType())) {
				PostDatabaseSettings settings = (PostDatabaseSettings) config.getSettings();

				statusPacketHandlers.add(new ThrottleFactorPacketHandler(
						new PostPacketHandler(settings.getUrl(), new JacksonStringPacketHandler(MAPPER), MediaType.get("application/json")),
						statusFrequencySettings,
						false
				));
			} else if (MqttDatabaseSettings.TYPE.equals(config.getType())) {
				MqttDatabaseSettings settings = (MqttDatabaseSettings) config.getSettings();

				String client = settings.getClientId();
				if (client == null) {
					client = "solarthing-" + sourceId + "-" + fragmentId;
				}

				statusPacketHandlers.add(new ThrottleFactorPacketHandler(
						new MqttPacketSaver(settings.getBroker(), client, settings.getUsername(), settings.getPassword(), settings.getTopicFormat(), settings.isRetain(), sourceId, fragmentId),
						statusFrequencySettings,
						true
				));
			}
		}
		return new PacketHandlerBundle(statusPacketHandlers, eventPacketHandlers);
	}

}

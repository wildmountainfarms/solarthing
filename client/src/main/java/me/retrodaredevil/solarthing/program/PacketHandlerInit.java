package me.retrodaredevil.solarthing.program;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.couchdb.CouchProperties;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.config.databases.IndividualSettings;
import me.retrodaredevil.solarthing.config.databases.implementations.CouchDbDatabaseSettings;
import me.retrodaredevil.solarthing.config.databases.implementations.InfluxDbDatabaseSettings;
import me.retrodaredevil.solarthing.config.databases.implementations.LatestFileDatabaseSettings;
import me.retrodaredevil.solarthing.config.databases.implementations.PostDatabaseSettings;
import me.retrodaredevil.solarthing.couchdb.CouchDbPacketSaver;
import me.retrodaredevil.solarthing.influxdb.ConstantDatabaseNameGetter;
import me.retrodaredevil.solarthing.influxdb.ConstantMeasurementPacketPointCreator;
import me.retrodaredevil.solarthing.influxdb.DocumentedMeasurementPacketPointCreator;
import me.retrodaredevil.solarthing.influxdb.InfluxDbPacketSaver;
import me.retrodaredevil.solarthing.influxdb.retention.ConstantRetentionPolicyGetter;
import me.retrodaredevil.solarthing.influxdb.retention.FrequentRetentionPolicyGetter;
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

public class PacketHandlerInit {
	private PacketHandlerInit(){ throw new UnsupportedOperationException(); }

	private static final Logger LOGGER = LoggerFactory.getLogger(PacketHandlerInit.class);
	private static final ObjectMapper MAPPER = JacksonUtil.defaultMapper();

	public static PacketHandlerBundle getPacketHandlerBundle(List<DatabaseConfig> configs, String uniqueStatusName, String uniqueEventName){
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
				InfluxDbDatabaseSettings settings = (InfluxDbDatabaseSettings) config.getSettings();
				String databaseName = settings.getDatabaseName();
				String measurementName = settings.getMeasurementName();
				statusPacketHandlers.add(new ThrottleFactorPacketHandler(
						new InfluxDbPacketSaver(
								settings.getInfluxProperties(),
								settings.getOkHttpProperties(),
								new ConstantDatabaseNameGetter(databaseName != null ? databaseName : uniqueStatusName),
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
						new ConstantDatabaseNameGetter(databaseName != null ? databaseName : uniqueEventName),
						measurementName != null
								? new ConstantMeasurementPacketPointCreator(measurementName)
								: (databaseName != null
										? new ConstantMeasurementPacketPointCreator(uniqueEventName)
										: DocumentedMeasurementPacketPointCreator.INSTANCE
								),
						new ConstantRetentionPolicyGetter(settings.getEventRetentionPolicy())
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
			}
		}
		return new PacketHandlerBundle(statusPacketHandlers, eventPacketHandlers);
	}

}

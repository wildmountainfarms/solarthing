package me.retrodaredevil.solarthing.program;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.action.ActionMultiplexer;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.couchdb.CouchDbUtil;
import me.retrodaredevil.solarthing.FragmentedPacketGroupProvider;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.actions.ActionNode;
import me.retrodaredevil.solarthing.actions.environment.*;
import me.retrodaredevil.solarthing.annotations.UtilityClass;
import me.retrodaredevil.solarthing.config.databases.implementations.CouchDbDatabaseSettings;
import me.retrodaredevil.solarthing.config.options.AutomationProgramOptions;
import me.retrodaredevil.solarthing.config.options.DatabaseTimeZoneOptionBase;
import me.retrodaredevil.solarthing.database.MillisDatabase;
import me.retrodaredevil.solarthing.database.MillisQuery;
import me.retrodaredevil.solarthing.database.SolarThingDatabase;
import me.retrodaredevil.solarthing.database.cache.SimpleDatabaseCache;
import me.retrodaredevil.solarthing.database.couchdb.CouchDbSolarThingDatabase;
import me.retrodaredevil.solarthing.database.exception.SolarThingDatabaseException;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.packets.collection.PacketGroup;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@UtilityClass
public final class AutomationMain {
	private AutomationMain() { throw new UnsupportedOperationException(); }

	private static final Logger LOGGER = LoggerFactory.getLogger(AutomationMain.class);
	private static final ObjectMapper CONFIG_MAPPER = JacksonUtil.defaultMapper();

	public static int startAutomation(AutomationProgramOptions options) throws IOException {
		List<ActionNode> actionNodes = new ArrayList<>();
		for (File file : options.getActionNodeFiles()) {
			actionNodes.add(CONFIG_MAPPER.readValue(file, ActionNode.class));
		}
		return startAutomation(actionNodes, options, options.getPeriodMillis());
	}

	private static void queryAndFeed(MillisDatabase millisDatabase, SimpleDatabaseCache databaseCache) {
		MillisQuery query = databaseCache.getRecommendedQuery();
		List<PacketGroup> rawPacketGroups = null;
		try {
			rawPacketGroups = millisDatabase.query(query);
			LOGGER.debug("Got packets from " + millisDatabase);
		} catch (SolarThingDatabaseException e) {
			LOGGER.error("Couldn't get status packets from " + millisDatabase, e);
		}
		if (rawPacketGroups != null) {
			databaseCache.feed(rawPacketGroups, query.getStartKey(), query.getEndKey());
			if (rawPacketGroups.isEmpty()) {
				LOGGER.info("packetGroups is empty! (Maybe there were no packets in the database?) millisDatabase=" + millisDatabase);
			}
		}
	}

	public static int startAutomation(List<ActionNode> actionNodes, DatabaseTimeZoneOptionBase options, long periodMillis) {
		LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Starting automation program.");
		final CouchDbDatabaseSettings couchSettings;
		try {
			couchSettings = ConfigUtil.expectCouchDbDatabaseSettings(options);
		} catch (IllegalArgumentException ex) {
			LOGGER.error("(Fatal)", ex);
			return SolarThingConstants.EXIT_CODE_INVALID_CONFIG;
		}
		SolarThingDatabase database = CouchDbSolarThingDatabase.create(CouchDbUtil.createInstance(couchSettings.getCouchProperties(), couchSettings.getOkHttpProperties()));

		VariableEnvironment variableEnvironment = new VariableEnvironment();

		FragmentedPacketGroup[] latestPacketGroupReference = new FragmentedPacketGroup[] { null };
		FragmentedPacketGroupProvider fragmentedPacketGroupProvider = () -> latestPacketGroupReference[0]; // note this may return null, and that's OK
		InjectEnvironment injectEnvironment = new InjectEnvironment.Builder()
				.add(new SourceIdEnvironment(options.getSourceId()))
				.add(new CouchDbEnvironment(couchSettings)) // most of the time, it's better to use SolarThingDatabaseEnvironment instead, but this option is here in case it's needed
				.add(new SolarThingDatabaseEnvironment(CouchDbSolarThingDatabase.create(CouchDbUtil.createInstance(couchSettings.getCouchProperties(), couchSettings.getOkHttpProperties()))))
				.add(new TimeZoneEnvironment(options.getTimeZone()))
				.add(new LatestPacketGroupEnvironment(fragmentedPacketGroupProvider))
				.add(new LatestFragmentedPacketGroupEnvironment(fragmentedPacketGroupProvider))
				.build();

		ActionMultiplexer multiplexer = new Actions.ActionMultiplexerBuilder().build();
		SimpleDatabaseCache statusDatabaseCache = SimpleDatabaseCache.createDefault();
//		SimpleDatabaseCache eventDatabaseCache = SimpleDatabaseCache.createDefault();
		while (!Thread.currentThread().isInterrupted()) {
			queryAndFeed(database.getStatusDatabase(), statusDatabaseCache);

			List<FragmentedPacketGroup> statusPacketGroups = PacketUtil.getPacketGroups(options.getSourceId(), options.getDefaultInstanceOptions(), statusDatabaseCache.getAllCachedPackets());
			if (statusPacketGroups != null) {
				FragmentedPacketGroup statusPacketGroup = statusPacketGroups.get(statusPacketGroups.size() - 1);
				latestPacketGroupReference[0] = statusPacketGroup;
			}
			for (ActionNode actionNode : actionNodes) {
				multiplexer.add(actionNode.createAction(new ActionEnvironment(variableEnvironment, new VariableEnvironment(), injectEnvironment)));
			}
			multiplexer.update();
			LOGGER.debug("There are " + multiplexer.getActiveActions().size() + " active actions");
			try {
				Thread.sleep(periodMillis);
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
				throw new RuntimeException(ex);
			}
		}
		return 0;
	}

}

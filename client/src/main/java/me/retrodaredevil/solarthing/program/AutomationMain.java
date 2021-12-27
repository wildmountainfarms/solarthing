package me.retrodaredevil.solarthing.program;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.action.ActionMultiplexer;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.action.node.environment.InjectEnvironment;
import me.retrodaredevil.action.node.environment.NanoTimeProviderEnvironment;
import me.retrodaredevil.action.node.environment.VariableEnvironment;
import me.retrodaredevil.action.node.util.NanoTimeProvider;
import me.retrodaredevil.couchdb.CouchDbUtil;
import me.retrodaredevil.solarthing.FragmentedPacketGroupProvider;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.solarthing.actions.environment.*;
import me.retrodaredevil.solarthing.annotations.UtilityClass;
import me.retrodaredevil.solarthing.config.databases.implementations.CouchDbDatabaseSettings;
import me.retrodaredevil.solarthing.config.options.AutomationProgramOptions;
import me.retrodaredevil.solarthing.config.options.DatabaseTimeZoneOptionBase;
import me.retrodaredevil.solarthing.database.*;
import me.retrodaredevil.solarthing.database.cache.SimpleDatabaseCache;
import me.retrodaredevil.solarthing.database.cache.SimplePacketCache;
import me.retrodaredevil.solarthing.database.couchdb.CouchDbSolarThingDatabase;
import me.retrodaredevil.solarthing.database.exception.SolarThingDatabaseException;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.packets.collection.StoredPacketGroup;
import me.retrodaredevil.solarthing.type.alter.StoredAlterPacket;
import me.retrodaredevil.solarthing.type.closed.authorization.AuthorizationPacket;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import me.retrodaredevil.solarthing.util.sync.BasicResourceManager;
import me.retrodaredevil.solarthing.util.sync.ResourceManager;
import me.retrodaredevil.solarthing.util.sync.SynchronizedResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


@UtilityClass
public final class AutomationMain {
	private AutomationMain() { throw new UnsupportedOperationException(); }

	private static final Logger LOGGER = LoggerFactory.getLogger(AutomationMain.class);
	private static final ObjectMapper CONFIG_MAPPER = ActionUtil.registerActionNodes(JacksonUtil.defaultMapper());

	public static int startAutomation(AutomationProgramOptions options) throws IOException {
		List<ActionNode> actionNodes = new ArrayList<>();
		for (File file : options.getActionNodeFiles()) {
			actionNodes.add(CONFIG_MAPPER.readValue(file, ActionNode.class));
		}
		return startAutomation(actionNodes, options, options.getPeriodMillis());
	}

	private static void queryAndFeed(MillisDatabase millisDatabase, ResourceManager<SimpleDatabaseCache> databaseCacheManager, boolean useEndDate) {
		final MillisQuery query = databaseCacheManager.access(databaseCache -> {
			if (useEndDate) {
				return databaseCache.getRecommendedQuery();
			} else {
				return databaseCache.createRecommendedQueryBuilder().endKey(null).build();
			}
		});

		final List<StoredPacketGroup> rawPacketGroups;
		try {
			rawPacketGroups = millisDatabase.query(query);
			LOGGER.debug("Got packets from " + millisDatabase);
		} catch (SolarThingDatabaseException e) {
			LOGGER.error("Couldn't get status packets from " + millisDatabase, e);
			return;
		}

		databaseCacheManager.update(databaseCache -> {
			databaseCache.feed(rawPacketGroups, query.getStartKey(), query.getEndKey());
		});
		if (rawPacketGroups.isEmpty()) {
			// This message is commonly printed for solarthing_open database, which is fine.
			LOGGER.debug("packetGroups is empty! millisDatabase=" + millisDatabase);
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

		AtomicReference<FragmentedPacketGroup> latestPacketGroupReference = new AtomicReference<>(null); // Use atomic reference so that access is thread safe
		AtomicReference<List<VersionedPacket<StoredAlterPacket>>> alterPacketsReference = new AtomicReference<>(null); // Use atomic reference so that access is thread safe
		FragmentedPacketGroupProvider fragmentedPacketGroupProvider = latestPacketGroupReference::get; // note this may return null, and that's OK // This is thread safe if needed
		Clock clock = Clock.systemUTC();

		SimpleDatabaseCache statusDatabaseCache = SimpleDatabaseCache.createDefault(clock);
		ResourceManager<SimpleDatabaseCache> statusDatabaseCacheManager = new BasicResourceManager<>(statusDatabaseCache); // not thread safe
		SimpleDatabaseCache eventDatabaseCache = SimpleDatabaseCache.createDefault(clock);
		ResourceManager<SimpleDatabaseCache> eventDatabaseCacheManager = new SynchronizedResourceManager<>(eventDatabaseCache);
		SimpleDatabaseCache openDatabaseCache = new SimpleDatabaseCache(Duration.ofMinutes(60), Duration.ofMinutes(40), Duration.ofMinutes(20), Duration.ofMinutes(15), clock);
		ResourceManager<SimpleDatabaseCache> openDatabaseCacheManager = new BasicResourceManager<>(openDatabaseCache); // not thread safe

		SimplePacketCache<AuthorizationPacket> authorizationPacketCache = new SimplePacketCache<>(Duration.ofSeconds(20), DatabaseDocumentKeyMap.createPacketSourceFromDatabase(database), false);
		String sourceId = options.getSourceId();
		InjectEnvironment injectEnvironment = new InjectEnvironment.Builder()
				.add(new NanoTimeProviderEnvironment(NanoTimeProvider.SYSTEM_NANO_TIME))
				.add(new SourceIdEnvironment(sourceId))
				.add(new CouchDbEnvironment(couchSettings)) // most of the time, it's better to use SolarThingDatabaseEnvironment instead, but this option is here in case it's needed
				.add(new SolarThingDatabaseEnvironment(CouchDbSolarThingDatabase.create(CouchDbUtil.createInstance(couchSettings.getCouchProperties(), couchSettings.getOkHttpProperties()))))
				.add(new TimeZoneEnvironment(options.getZoneId()))
				.add(new LatestPacketGroupEnvironment(fragmentedPacketGroupProvider)) // access is thread safe if needed
				.add(new LatestFragmentedPacketGroupEnvironment(fragmentedPacketGroupProvider)) // access is thread safe if needed
				.add(new EventDatabaseCacheEnvironment(eventDatabaseCacheManager))
				.add(new OpenDatabaseCacheEnvironment(openDatabaseCache))
				.add(new AlterPacketsEnvironment(alterPacketsReference::get)) // access is thread safe if needed
				.add(new AuthorizationEnvironment(new DatabaseDocumentKeyMap(authorizationPacketCache)))
				.build();

		ActionMultiplexer multiplexer = new Actions.ActionMultiplexerBuilder().build();
		while (!Thread.currentThread().isInterrupted()) {
			queryAndFeed(database.getStatusDatabase(), statusDatabaseCacheManager, true);
			queryAndFeed(database.getEventDatabase(), eventDatabaseCacheManager, true);
			queryAndFeed(database.getOpenDatabase(), openDatabaseCacheManager, false);
			{
				// Never cache alter packets, because it's always important that we have up-to-date data, or no data at all.
				List<VersionedPacket<StoredAlterPacket>> alterPackets = null;
				try {
					alterPackets = database.getAlterDatabase().queryAll(sourceId);
					LOGGER.debug("Got " + alterPackets.size() + " alter packets");
				} catch (SolarThingDatabaseException e) {
					LOGGER.error("Could not get alter packets", e);
				}
				alterPacketsReference.set(alterPackets);
			}
			authorizationPacketCache.updateIfNeeded(); // we have auto update turned off, so we have to call this

			List<FragmentedPacketGroup> statusPacketGroups = PacketUtil.getPacketGroups(options.getSourceId(), options.getDefaultInstanceOptions(), statusDatabaseCache.getAllCachedPackets());
			if (statusPacketGroups != null) {
				FragmentedPacketGroup statusPacketGroup = statusPacketGroups.get(statusPacketGroups.size() - 1);
				latestPacketGroupReference.set(statusPacketGroup);
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

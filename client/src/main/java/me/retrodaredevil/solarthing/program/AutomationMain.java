package me.retrodaredevil.solarthing.program;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import me.retrodaredevil.action.ActionMultiplexer;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.couchdb.CouchDbUtil;
import me.retrodaredevil.couchdbjava.ViewQueryParamsBuilder;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.actions.ActionNode;
import me.retrodaredevil.solarthing.actions.environment.*;
import me.retrodaredevil.solarthing.annotations.UtilityClass;
import me.retrodaredevil.solarthing.config.databases.implementations.CouchDbDatabaseSettings;
import me.retrodaredevil.solarthing.config.options.AutomationProgramOptions;
import me.retrodaredevil.solarthing.config.options.DatabaseTimeZoneOptionBase;
import me.retrodaredevil.solarthing.couchdb.CouchDbQueryHandler;
import me.retrodaredevil.solarthing.couchdb.SolarThingCouchDb;
import me.retrodaredevil.solarthing.misc.device.DevicePacket;
import me.retrodaredevil.solarthing.misc.error.ErrorPacket;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.packets.collection.parsing.LenientPacketParser;
import me.retrodaredevil.solarthing.packets.collection.parsing.MultiPacketConverter;
import me.retrodaredevil.solarthing.packets.collection.parsing.PacketGroupParser;
import me.retrodaredevil.solarthing.packets.collection.parsing.SimplePacketGroupParser;
import me.retrodaredevil.solarthing.packets.handling.PacketHandleException;
import me.retrodaredevil.solarthing.packets.instance.InstancePacket;
import me.retrodaredevil.solarthing.solar.SolarStatusPacket;
import me.retrodaredevil.solarthing.solar.extra.SolarExtraPacket;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

@UtilityClass
public final class AutomationMain {
	private AutomationMain() { throw new UnsupportedOperationException(); }

	private static final Logger LOGGER = LoggerFactory.getLogger(AutomationMain.class);
	private static final ObjectMapper CONFIG_MAPPER = JacksonUtil.defaultMapper();
	private static final ObjectMapper PARSE_MAPPER = JacksonUtil.lenientMapper(JacksonUtil.defaultMapper());
	public static int startAutomation(AutomationProgramOptions options) throws IOException {
		List<ActionNode> actionNodes = new ArrayList<>();
		for (File file : options.getActionNodeFiles()) {
			actionNodes.add(CONFIG_MAPPER.readValue(file, ActionNode.class));
		}
		return startAutomation(actionNodes, options, options.getPeriodMillis());
	}
	public static int startAutomation(List<ActionNode> actionNodes, DatabaseTimeZoneOptionBase options, long periodMillis) {
		LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Starting automation program.");
		CouchDbDatabaseSettings couchSettings = ConfigUtil.expectCouchDbDatabaseSettings(options);
		CouchDbQueryHandler queryHandler = new CouchDbQueryHandler(
				CouchDbUtil.createInstance(couchSettings.getCouchProperties(), couchSettings.getOkHttpProperties())
						.getDatabase(SolarThingConstants.SOLAR_STATUS_UNIQUE_NAME)
		);
		PacketGroupParser statusParser = new SimplePacketGroupParser(new LenientPacketParser(
				MultiPacketConverter.createFrom(PARSE_MAPPER, SolarStatusPacket.class, SolarExtraPacket.class, DevicePacket.class, ErrorPacket.class, InstancePacket.class)
		));

		VariableEnvironment variableEnvironment = new VariableEnvironment();

		FragmentedPacketGroup last = null;
		FragmentedPacketGroup[] latestPacketGroupReference = new FragmentedPacketGroup[1];
		InjectEnvironment injectEnvironment = new InjectEnvironment.Builder()
				.add(new SourceIdEnvironment(options.getSourceId()))
				.add(new CouchDbEnvironment(couchSettings))
				.add(new TimeZoneEnvironment(options.getTimeZone()))
				.add(new LatestPacketGroupEnvironment(() -> requireNonNull(latestPacketGroupReference[0])))
				.build();

		ActionMultiplexer multiplexer = new Actions.ActionMultiplexerBuilder().build();
		while (!Thread.currentThread().isInterrupted()) {
			List<ObjectNode> statusPacketNodes = null;
			try {
				long now = System.currentTimeMillis();
				statusPacketNodes = queryHandler.query(SolarThingCouchDb.createMillisView(new ViewQueryParamsBuilder()
						.startKey(now - 5 * 60 * 1000)
						.endKey(now).build()));
				LOGGER.debug("Got packets");
			} catch (PacketHandleException e) {
				LOGGER.error("Couldn't get status packets", e);
			}
			if(statusPacketNodes != null) {
				List<FragmentedPacketGroup> packetGroups = PacketUtil.getPacketGroups(options.getSourceId(), options.getDefaultInstanceOptions(), statusPacketNodes, statusParser);
				if (packetGroups != null) {
					FragmentedPacketGroup packetGroup = packetGroups.get(packetGroups.size() - 1);
					latestPacketGroupReference[0] = packetGroup;
					if (last != null && last.getDateMillis() >= packetGroup.getDateMillis()) {
						LOGGER.debug("No new packets! last date=" + last.getDateMillis() + " current packet date=" + packetGroup.getDateMillis());
					} else {
						for (ActionNode actionNode : actionNodes) {
							multiplexer.add(actionNode.createAction(new ActionEnvironment(variableEnvironment, new VariableEnvironment(), injectEnvironment)));
						}
						multiplexer.update();
						LOGGER.debug("There are " + multiplexer.getActiveActions().size() + " active actions");
					}
					last = packetGroup;
				}
			}
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

package me.retrodaredevil.solarthing.program;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.ActionMultiplexer;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.couchdb.CouchProperties;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.actions.ActionNode;
import me.retrodaredevil.solarthing.actions.environment.*;
import me.retrodaredevil.solarthing.config.options.DatabaseTimeZoneOptionBase;
import me.retrodaredevil.solarthing.config.options.MessageSenderProgramOptions;
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

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class AutomationMain {
	private static final Logger LOGGER = LoggerFactory.getLogger(AutomationMain.class);
	private static final ObjectMapper CONFIG_MAPPER = JacksonUtil.defaultMapper();
	private static final ObjectMapper PARSE_MAPPER = JacksonUtil.lenientMapper(JacksonUtil.defaultMapper());

	public static int startAutomation(List<ActionNode> actionNodes, DatabaseTimeZoneOptionBase options) {
		LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Starting automation program.");
		CouchProperties couchProperties = SolarMain.createCouchProperties(options);
		CouchDbQueryHandler queryHandler = SolarMain.createCouchDbQueryHandler(couchProperties);
		PacketGroupParser statusParser = new SimplePacketGroupParser(new LenientPacketParser(
				MultiPacketConverter.createFrom(PARSE_MAPPER, SolarStatusPacket.class, SolarExtraPacket.class, DevicePacket.class, ErrorPacket.class, InstancePacket.class)
		));

		VariableEnvironment variableEnvironment = new VariableEnvironment();

		FragmentedPacketGroup last = null;
		FragmentedPacketGroup[] latestPacketGroupReference = new FragmentedPacketGroup[1];
		InjectEnvironment injectEnvironment = new InjectEnvironment.Builder()
				.add(new SourceIdEnvironment(options.getSourceId()))
				.add(new CouchDbEnvironment(couchProperties))
				.add(new LatestPacketGroupEnvironment(() -> requireNonNull(latestPacketGroupReference[0])))
				.build();

		ActionMultiplexer multiplexer = new Actions.ActionMultiplexerBuilder().build();
		while (!Thread.currentThread().isInterrupted()) {
			List<ObjectNode> statusPacketNodes = null;
			try {
				long now = System.currentTimeMillis();
				statusPacketNodes = queryHandler.query(SolarThingCouchDb.createMillisView()
						.startKey(now - 5 * 60 * 1000)
						.endKey(now));
				LOGGER.debug("Got packets");
			} catch (PacketHandleException e) {
				LOGGER.error("Couldn't get status packets", e);
			}
			if(statusPacketNodes != null) {
				List<FragmentedPacketGroup> packetGroups = PacketUtil.getPacketGroups(options.getSourceId(), options.getDefaultInstanceOptions(), statusPacketNodes, statusParser);
				if (packetGroups != null) {
					FragmentedPacketGroup packetGroup = packetGroups.get(packetGroups.size() - 1);
					latestPacketGroupReference[0] = packetGroup;
					if (last != null) {
						if (last.getDateMillis() >= packetGroup.getDateMillis()) {
							LOGGER.warn("No new packets! last date=" + last.getDateMillis() + " current packet date=" + packetGroup.getDateMillis());
						} else {
							for (ActionNode actionNode : actionNodes) {
								multiplexer.add(actionNode.createAction(new ActionEnvironment(variableEnvironment, new VariableEnvironment(), injectEnvironment)));
							}
							multiplexer.update();
						}
					}
					last = packetGroup;
				}
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
				throw new RuntimeException(ex);
			}
		}
		return 0;
	}
}

package me.retrodaredevil.solarthing.mqtt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import me.retrodaredevil.solarthing.InstantType;
import me.retrodaredevil.solarthing.influxdb.PointUtil;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.*;
import me.retrodaredevil.solarthing.packets.handling.PacketHandleException;
import me.retrodaredevil.solarthing.packets.handling.PacketHandler;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class MqttPacketSaver implements PacketHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(MqttPacketSaver.class);
	private static final ObjectMapper OBJECT_MAPPER = JacksonUtil.defaultMapper();
	private static final Charset CHARSET = StandardCharsets.UTF_8;
	/** The default topic format. Note that the ending "/" is optional. */
	public static final String DEFAULT_TOPIC_FORMAT = "solarthing/%source/%fragment/%identifier";

	private final MemoryPersistence persistence = new MemoryPersistence();
	private final MqttClient client;
	private final MqttConnectOptions options;
	/** The topic format similar to "solarthing/%source/%fragment/%identifier" (NO slash at end) */
	private final String topicFormat;
	private final String statusTopic;
	private final boolean retain;

	public MqttPacketSaver(String broker, String clientId, String username, char[] password, String topicFormat, boolean retain, String sourceId, int fragmentId) {
		this.topicFormat = topicFormat;
		this.retain = retain;

//		org.eclipse.paho.client.mqttv3.logging.LoggerFactory.setLogger();
		try {
			client = new MqttClient(broker, clientId, persistence);
		} catch (MqttPersistenceException ex) {
			throw new RuntimeException("This shouldn't happen when using MemoryPersistence", ex);
		} catch (MqttException ex) {
			throw new RuntimeException("I have no idea what exception this could be", ex);
		}
		options = new MqttConnectOptions();
		options.setUserName(username);
		options.setPassword(password);
		statusTopic = topicFormat.replace("%source", sourceId)
				.replace("%fragment", "" + fragmentId)
				.replace("%identifier", "status");
		options.setWill(
				statusTopic,
				"offline".getBytes(CHARSET),
				1,
				retain
		);
	}

	@Override
	public void handle(PacketCollection packetCollection, InstantType instantType) throws PacketHandleException {
		InstancePacketGroup instancePacketGroup = PacketGroups.parseToInstancePacketGroup(packetCollection, DefaultInstanceOptions.REQUIRE_NO_DEFAULTS);
		try {
			handle(instancePacketGroup, packetCollection);
		} catch (MqttException e) {
			throw new PacketHandleException("MQTT Exception", e);
		} finally {
			if (client.isConnected()) {
				try {
					client.disconnect();
				} catch (MqttException e) {
					LOGGER.error("We shouldn't get an exception while disconnecting... (Can't throw in finally)", e);
				}
			}
		}
	}
	private void handle(InstancePacketGroup instancePacketGroup, PacketCollection entirePacketCollection) throws MqttException {
		DefaultInstanceOptions.requireNoDefaults(instancePacketGroup);
		long dateMillis = instancePacketGroup.getDateMillis();
		final String partiallyFormattedTopic = topicFormat
				.replace("%source", instancePacketGroup.getSourceId())
				.replace("%fragment", "" + instancePacketGroup.getFragmentId());

		client.connect(options);
		{ // block for entire packet collection
			final String collectionJson;
			try {
				collectionJson = OBJECT_MAPPER.writeValueAsString(entirePacketCollection);
			} catch (JsonProcessingException e) {
				throw new RuntimeException("We should be able to serial this to JSON!", e);
			}
			client.publish(
					partiallyFormattedTopic.replace("%identifier", "packetCollection"),
					collectionJson.getBytes(CHARSET),
					2,
					retain
			);
		}
		client.publish(statusTopic, "online".getBytes(CHARSET), 1, retain);

		for (Packet packet : instancePacketGroup.getPackets()) {
			if (packet instanceof Identifiable) {
				Identifiable identifiable = (Identifiable) packet;
				String topic = partiallyFormattedTopic.replace("%identifier", identifiable.getIdentifier().getRepresentation()) + "/";


				final String rawJson;
				try {
					rawJson = OBJECT_MAPPER.writeValueAsString(packet);
				} catch (JsonProcessingException e) {
					throw new RuntimeException("We should be able to serialize this to JSON!", e);
				}
				client.publish(topic + "dateMillis", ("" + dateMillis).getBytes(CHARSET), 2, retain);
				client.publish(topic + "json", rawJson.getBytes(CHARSET), 2, retain);
				ObjectNode json = OBJECT_MAPPER.valueToTree(packet);
				for (Map.Entry<String, ValueNode> entry : PointUtil.flattenJsonObject(json, "/")) {
					String key = entry.getKey();
					ValueNode prim = entry.getValue();
					// Make individual fields use QoS of 1 because we want them all to go through, but no one should be using them as "event"-like packets
					client.publish(topic + "packet/" + key, prim.asText().getBytes(CHARSET), 1, retain);
				}
			}
		}
	}
}

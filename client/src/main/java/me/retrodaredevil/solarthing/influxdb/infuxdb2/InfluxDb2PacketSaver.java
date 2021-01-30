package me.retrodaredevil.solarthing.influxdb.infuxdb2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.InfluxDBClientOptions;
import com.influxdb.client.write.Point;
import me.retrodaredevil.influxdb.influxdb1.InfluxProperties;
import me.retrodaredevil.influxdb.influxdb2.InfluxDb2Properties;
import me.retrodaredevil.okhttp3.OkHttpProperties;
import me.retrodaredevil.solarthing.InstantType;
import me.retrodaredevil.solarthing.influxdb.NameGetter;
import me.retrodaredevil.solarthing.influxdb.influxdb1.InfluxDbPacketSaver;
import me.retrodaredevil.solarthing.influxdb.influxdb1.PacketPointCreator;
import me.retrodaredevil.solarthing.influxdb.retention.RetentionPolicyGetter;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.handling.PacketHandleException;
import me.retrodaredevil.solarthing.packets.handling.PacketHandler;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.requireNonNull;

public class InfluxDb2PacketSaver implements PacketHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(InfluxDbPacketSaver.class);
	private static final Logger INFLUX_LOGGER = LoggerFactory.getLogger("org.influxdb");
	private static final ObjectMapper OBJECT_MAPPER = JacksonUtil.defaultMapper();

	private final InfluxDb2Properties properties;
	private final OkHttpProperties okHttpProperties;
	private final NameGetter bucketNameGetter;

	private InfluxDBClient createClient() {
		InfluxDBClientOptions.Builder builder = new InfluxDBClientOptions.Builder();
		builder.url(properties.getUrl());
		char[] token = properties.getToken();
		if (token != null) {
			builder.authenticateToken(token);
		} else {
			String username = requireNonNull(properties.getUsername(), "If no token, must provide username and password");
			char[] password = requireNonNull(properties.getPassword(), "If no token, must provide username and password");
			builder.authenticate(username, password);
		}
		String org = properties.getOrg();
		if (org != null) {
			builder.org(org);
		}
		return InfluxDBClientFactory.create(builder.build());
	}

	@Override
	public void handle(PacketCollection packetCollection, InstantType instantType) throws PacketHandleException {
	}

}

package me.retrodaredevil.solarthing.influxdb.infuxdb2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.DecimalNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import com.influxdb.LogLevel;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.InfluxDBClientOptions;
import com.influxdb.client.domain.Bucket;
import com.influxdb.client.domain.Organization;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.exceptions.InfluxException;
import me.retrodaredevil.influxdb.influxdb2.InfluxDb2Properties;
import me.retrodaredevil.okhttp3.OkHttpProperties;
import me.retrodaredevil.okhttp3.OkHttpUtil;
import me.retrodaredevil.solarthing.influxdb.NameGetter;
import me.retrodaredevil.solarthing.influxdb.PointUtil;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.DefaultInstanceOptions;
import me.retrodaredevil.solarthing.packets.collection.InstancePacketGroup;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.collection.PacketGroups;
import me.retrodaredevil.solarthing.packets.handling.PacketHandleException;
import me.retrodaredevil.solarthing.packets.handling.PacketHandler;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class InfluxDb2PacketSaver implements PacketHandler {
//	private static final Logger LOGGER = LoggerFactory.getLogger(InfluxDbPacketSaver.class);
	private static final Logger INFLUX_LOGGER = LoggerFactory.getLogger("org.influxdb");
	private static final ObjectMapper OBJECT_MAPPER = JacksonUtil.defaultMapper();


	private final InfluxDBClient client;

	private final InfluxDb2Properties properties;
	private final NameGetter bucketNameGetter;
	private final PacketPoint2Creator pointCreator;

	public InfluxDb2PacketSaver(InfluxDb2Properties properties, OkHttpProperties okHttpProperties, NameGetter bucketNameGetter, PacketPoint2Creator pointCreator) {
		client = createClient(properties, okHttpProperties);
		this.properties = properties;
		this.bucketNameGetter = bucketNameGetter;
		this.pointCreator = pointCreator;
	}

	private static InfluxDBClient createClient(InfluxDb2Properties properties, OkHttpProperties okHttpProperties) {
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
		builder.org(properties.getOrg());
		builder.okHttpClient(OkHttpUtil.createBuilder(okHttpProperties)
				.addInterceptor(new HttpLoggingInterceptor(INFLUX_LOGGER::info).setLevel(HttpLoggingInterceptor.Level.BASIC)));
		return InfluxDBClientFactory.create(builder.build())
				.setLogLevel(LogLevel.NONE)
				.enableGzip();
	}
	private Organization findOrCreateOrg() throws PacketHandleException {
		final List<Organization> organizations;
		try {
			organizations = client.getOrganizationsApi().findOrganizations();
		} catch (InfluxException ex) {
			throw new PacketHandleException("Couldn't find organizations", ex);
		}
		for (Organization organization : organizations) {
			if (organization.getName().equals(properties.getOrg())) {
				return organization;
			}
		}
		try {
			return client.getOrganizationsApi().createOrganization(properties.getOrg());
		} catch (InfluxException ex) {
			throw new PacketHandleException("Couldn't create organization", ex);
		}
	}
	private Bucket findOrCreateBucket(String name, Organization organization) throws PacketHandleException {
		final Bucket r;
		try {
			r = client.getBucketsApi().findBucketByName(name);
		} catch (InfluxException ex) {
			throw new PacketHandleException("Couldn't find bucket", ex);
		}
		if (r == null) {
			try {
				return client.getBucketsApi().createBucket(name, organization.getId());
			} catch (InfluxException ex) {
				throw new PacketHandleException("Couldn't create bucket", ex);
			}
		}
		return r;
	}

	@Override
	public void handle(PacketCollection packetCollection) throws PacketHandleException {
		final InstancePacketGroup packetGroup = PacketGroups.parseToInstancePacketGroup(packetCollection, DefaultInstanceOptions.REQUIRE_NO_DEFAULTS);
		DefaultInstanceOptions.requireNoDefaults(packetGroup);
		Organization organization = findOrCreateOrg();
		Bucket bucket = findOrCreateBucket(bucketNameGetter.getName(packetGroup), organization);

		final long time = packetCollection.getDateMillis();
		List<Point> points = new ArrayList<>();
		for (Packet packet : packetGroup.getPackets()) {
			Point point = pointCreator.createBuilder(packet).time(time, WritePrecision.MS);

			Collection<String> tagKeys = PointUtil.getTagKeys(packet.getClass());
			ObjectNode json = OBJECT_MAPPER.valueToTree(packet);
			for (Map.Entry<String, ValueNode> entry : PointUtil.flattenJsonObject(json)) {
				String key = entry.getKey();
				ValueNode prim = entry.getValue();
				if(tagKeys.contains(key)){
					point.addTag(key, prim.asText());
				}
				if (prim.isNumber()) {
					// always store as float datatype because you can never change the type from int to float easily
					final Number value;
					if (prim.isBigDecimal()) {
						DecimalNode decimal = (DecimalNode) prim;
						value = decimal.decimalValue();
					} else {
						value = prim.asDouble();
					}
					point.addField(key, value);
				} else if (prim.isTextual() || prim.isBinary()) {
					point.addField(key, prim.asText());
				} else if (prim.isBoolean()) {
					point.addField(key, prim.asBoolean());
				} else throw new AssertionError("This primitive isn't a number, string/binary or boolean! It's: " + prim + " class: " + prim.getClass() + " text=" + prim.asText());
			}
			points.add(point);
		}
		try {
			client.getWriteApiBlocking().writePoints(bucket.getName(), bucket.getOrgID(), points);
		} catch (InfluxException exception) {
			throw new PacketHandleException("Could not write points", exception);
		}
	}

}

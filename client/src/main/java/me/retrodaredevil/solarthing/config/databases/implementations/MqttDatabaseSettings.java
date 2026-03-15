package me.retrodaredevil.solarthing.config.databases.implementations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.config.databases.DatabaseSettings;
import me.retrodaredevil.solarthing.config.databases.DatabaseType;
import me.retrodaredevil.solarthing.config.databases.SimpleDatabaseType;
import me.retrodaredevil.solarthing.mqtt.MqttPacketSaver;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static java.util.Objects.requireNonNull;

@JsonTypeName("mqtt")
@NullMarked
public class MqttDatabaseSettings implements DatabaseSettings {
	public static final DatabaseType TYPE = new SimpleDatabaseType("mqtt");

	private final String broker;
	private final @Nullable String clientId;
	private final String username;
	private final char[] password;
	private final String topicFormat;
	private final boolean retain;

	@JsonCreator
	public MqttDatabaseSettings(
			@JsonProperty(value = "broker", required = true) String broker,
			@JsonProperty("client_id") @Nullable String clientId,
			@JsonProperty(value = "username", required = true) String username,
			@JsonProperty(value = "password", required = true) char[] password,
			@JsonProperty("topic") @Nullable String topicFormat,
			@JsonProperty("retain") @Nullable Boolean retain) {
		this.broker = requireNonNull(broker);
		this.clientId = clientId;
		this.username = requireNonNull(username);
		this.password = requireNonNull(password);
		this.topicFormat = topicFormat == null ? MqttPacketSaver.DEFAULT_TOPIC_FORMAT : topicFormat;
		this.retain = retain == null || retain; // default to true
	}

	@Override
	public String toString() {
		return "MQTT " + broker;
	}

	@Override
	public DatabaseType getDatabaseType() {
		return TYPE;
	}

	public String getBroker() {
		return broker;
	}

	public @Nullable String getClientId() {
		return clientId;
	}

	public String getUsername() {
		return username;
	}

	public char[] getPassword() {
		return password;
	}

	public String getTopicFormat() {
		return topicFormat;
	}

	public boolean isRetain() {
		return retain;
	}
}

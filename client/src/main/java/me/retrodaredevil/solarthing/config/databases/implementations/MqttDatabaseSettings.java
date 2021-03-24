package me.retrodaredevil.solarthing.config.databases.implementations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.config.databases.DatabaseSettings;
import me.retrodaredevil.solarthing.config.databases.DatabaseType;
import me.retrodaredevil.solarthing.config.databases.SimpleDatabaseType;
import me.retrodaredevil.solarthing.mqtt.MqttPacketSaver;

import static java.util.Objects.requireNonNull;

@JsonTypeName("mqtt")
public class MqttDatabaseSettings implements DatabaseSettings {
	public static final DatabaseType TYPE = new SimpleDatabaseType("mqtt");

	private final String broker;
	private final String clientId;
	private final String username;
	private final char[] password;
	private final String topicFormat;
	private final boolean retain;

	@JsonCreator
	public MqttDatabaseSettings(
			@JsonProperty(value = "broker", required = true) String broker,
			@JsonProperty("client_id") String clientId,
			@JsonProperty(value = "username", required = true) String username,
			@JsonProperty(value = "password", required = true) char[] password,
			@JsonProperty("topic") String topicFormat,
			@JsonProperty("retain") Boolean retain) {
		requireNonNull(this.broker = broker);
		this.clientId = clientId;
		requireNonNull(this.username = username);
		requireNonNull(this.password = password);
		this.topicFormat = topicFormat == null ? MqttPacketSaver.DEFAULT_TOPIC_FORMAT : topicFormat;
		this.retain = retain == null || retain; // default to true
	}

	@Override
	public DatabaseType getDatabaseType() {
		return TYPE;
	}

	public @NotNull String getBroker() {
		return broker;
	}

	public @Nullable String getClientId() {
		return clientId;
	}

	public @NotNull String getUsername() {
		return username;
	}

	public @NotNull char[] getPassword() {
		return password;
	}

	public @NotNull String getTopicFormat() {
		return topicFormat;
	}

	public boolean isRetain() {
		return retain;
	}
}

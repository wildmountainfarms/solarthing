package me.retrodaredevil.solarthing.config.databases.implementations;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.influxdb.influxdb1.InfluxProperties;
import me.retrodaredevil.solarthing.jackson.UnwrappedDeserializer;
import me.retrodaredevil.okhttp3.OkHttpProperties;
import me.retrodaredevil.solarthing.config.databases.DatabaseSettings;
import me.retrodaredevil.solarthing.config.databases.DatabaseType;
import me.retrodaredevil.solarthing.config.databases.SimpleDatabaseType;
import me.retrodaredevil.solarthing.influxdb.retention.RetentionPolicySetting;
import me.retrodaredevil.solarthing.util.frequency.FrequentObject;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;

import static java.util.Objects.requireNonNull;

@JsonTypeName("influxdb")
//@JsonDeserialize(builder = InfluxDbDatabaseSettings.Builder.class)
@JsonDeserialize(using = InfluxDbDatabaseSettings.Deserializer.class)
@NullMarked
public final class InfluxDbDatabaseSettings implements DatabaseSettings {
	public static final DatabaseType TYPE = new SimpleDatabaseType("influxdb");

	private final InfluxProperties influxProperties;
	private final OkHttpProperties okHttpProperties;
	private final @Nullable String databaseName;
	private final @Nullable String measurementName;
	private final List<FrequentObject<RetentionPolicySetting>> frequentStatusRetentionPolicyList;

	private final @Nullable RetentionPolicySetting eventRetentionPolicySetting;

	public InfluxDbDatabaseSettings(InfluxProperties influxProperties, OkHttpProperties okHttpProperties, @Nullable String databaseName, @Nullable String measurementName, Collection<FrequentObject<RetentionPolicySetting>> frequentRetentionPolicies, @Nullable RetentionPolicySetting eventRetentionPolicySetting) {
		this.influxProperties = requireNonNull(influxProperties);
		this.okHttpProperties = requireNonNull(okHttpProperties);
		this.databaseName = databaseName;
		this.measurementName = measurementName;
		this.frequentStatusRetentionPolicyList = List.copyOf(frequentRetentionPolicies);
		this.eventRetentionPolicySetting = eventRetentionPolicySetting;
	}

	@Override
	public String toString() {
		return "InfluxDB1.X " + influxProperties.getUrl();
	}

	@Override
	public DatabaseType getDatabaseType() {
		return TYPE;
	}

	public InfluxProperties getInfluxProperties(){ return influxProperties; }

	public OkHttpProperties getOkHttpProperties() {
		return okHttpProperties;
	}

	/**
	 * @return The database name, or null to use
	 */
	public @Nullable String getDatabaseName(){ return databaseName; }
	public @Nullable String getMeasurementName() { return measurementName; }

	public List<FrequentObject<RetentionPolicySetting>> getFrequentStatusRetentionPolicyList() {
		return frequentStatusRetentionPolicyList;
	}
	public @Nullable RetentionPolicySetting getEventRetentionPolicy(){
		return eventRetentionPolicySetting;
	}
	@Deprecated
	static class Deserializer extends UnwrappedDeserializer<InfluxDbDatabaseSettings, Builder> {
		Deserializer() {
			super(Builder.class, Builder::build);
		}
	}
	public static class Builder {
		@JsonUnwrapped
		private @Nullable InfluxProperties influxProperties;
		@JsonUnwrapped
		private @Nullable OkHttpProperties okHttpProperties;
		@JsonProperty("database")
		private @Nullable String databaseName;
		@JsonProperty("measurement")
		private @Nullable String measurementName;
		@JsonProperty("status_retention_policies")
		private @Nullable List<FrequentObject<RetentionPolicySetting>> frequentStatusRetentionPolicies;
		@JsonProperty("event_retention_policy")
		private @Nullable RetentionPolicySetting eventRetentionPolicySetting;

		public InfluxDbDatabaseSettings build() {
			return new InfluxDbDatabaseSettings(
					requireNonNull(influxProperties, "JsonUnwrapped should have initialized influxProperties"),
					requireNonNull(okHttpProperties, "JsonUnwrapped should have initialized okHttpProperties"),
					databaseName,
					measurementName,
					requireNonNull(frequentStatusRetentionPolicies, "status_retention_policies must be configured"),
					eventRetentionPolicySetting
			);
		}
	}
}

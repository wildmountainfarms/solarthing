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

import java.util.*;

import static java.util.Objects.requireNonNull;

@JsonTypeName("influxdb")
//@JsonDeserialize(builder = InfluxDbDatabaseSettings.Builder.class)
@JsonDeserialize(using = InfluxDbDatabaseSettings.Deserializer.class)
public final class InfluxDbDatabaseSettings implements DatabaseSettings {
	public static final DatabaseType TYPE = new SimpleDatabaseType("influxdb");

	private final InfluxProperties influxProperties;
	private final OkHttpProperties okHttpProperties;
	private final String databaseName;
	private final String measurementName;
	private final List<FrequentObject<RetentionPolicySetting>> frequentStatusRetentionPolicyList;

	private final RetentionPolicySetting eventRetentionPolicySetting;

	public InfluxDbDatabaseSettings(InfluxProperties influxProperties, OkHttpProperties okHttpProperties, String databaseName, String measurementName, Collection<FrequentObject<RetentionPolicySetting>> frequentRetentionPolicies, RetentionPolicySetting eventRetentionPolicySetting) {
		this.influxProperties = requireNonNull(influxProperties);
		this.okHttpProperties = requireNonNull(okHttpProperties);
		this.databaseName = databaseName;
		this.measurementName = measurementName;
		this.frequentStatusRetentionPolicyList = Collections.unmodifiableList(new ArrayList<>(frequentRetentionPolicies));
		this.eventRetentionPolicySetting = eventRetentionPolicySetting;
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
	public String getDatabaseName(){ return databaseName; }
	public String getMeasurementName() { return measurementName; }

	public List<FrequentObject<RetentionPolicySetting>> getFrequentStatusRetentionPolicyList() {
		return frequentStatusRetentionPolicyList;
	}
	public RetentionPolicySetting getEventRetentionPolicy(){
		return eventRetentionPolicySetting;
	}
	static class Deserializer extends UnwrappedDeserializer<InfluxDbDatabaseSettings, Builder> {
		Deserializer() {
			super(Builder.class, Builder::build);
		}
	}
	public static class Builder {
		@JsonUnwrapped
		private InfluxProperties influxProperties;
		@JsonUnwrapped
		private OkHttpProperties okHttpProperties;
		@JsonProperty("database")
		private String databaseName;
		@JsonProperty("measurement")
		private String measurementName;
		@JsonProperty("status_retention_policies")
		private List<FrequentObject<RetentionPolicySetting>> frequentStatusRetentionPolicies;
		@JsonProperty("event_retention_policy")
		private RetentionPolicySetting eventRetentionPolicySetting;

		public InfluxDbDatabaseSettings build() {
			return new InfluxDbDatabaseSettings(influxProperties, okHttpProperties, databaseName, measurementName, frequentStatusRetentionPolicies, eventRetentionPolicySetting);
		}
	}
}

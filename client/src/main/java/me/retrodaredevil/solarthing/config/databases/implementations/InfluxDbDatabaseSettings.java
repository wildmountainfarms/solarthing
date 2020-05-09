package me.retrodaredevil.solarthing.config.databases.implementations;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import me.retrodaredevil.influxdb.InfluxProperties;
import me.retrodaredevil.okhttp3.OkHttpProperties;
import me.retrodaredevil.solarthing.config.databases.DatabaseSettings;
import me.retrodaredevil.solarthing.config.databases.DatabaseType;
import me.retrodaredevil.solarthing.config.databases.SimpleDatabaseType;
import me.retrodaredevil.solarthing.influxdb.retention.RetentionPolicySetting;
import me.retrodaredevil.solarthing.util.frequency.FrequentObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.requireNonNull;

@JsonTypeName("influxdb")
@JsonDeserialize(builder = InfluxDbDatabaseSettings.Builder.class)
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
	@JsonPOJOBuilder
	public static class Builder {
		private InfluxProperties influxProperties;
		private OkHttpProperties okHttpProperties;
		private String databaseName;
		private String measurementName;
		private Collection<FrequentObject<RetentionPolicySetting>> frequentStatusRetentionPolicies;
		private RetentionPolicySetting eventRetentionPolicySetting;

		// TODO fix this https://github.com/FasterXML/jackson-databind/issues/1061
		@JsonUnwrapped
		public Builder setInfluxProperties(InfluxProperties influxProperties) {
			this.influxProperties = influxProperties;
			return this;
		}

		@JsonUnwrapped
		public Builder setOkHttpProperties(OkHttpProperties okHttpProperties) {
			this.okHttpProperties = okHttpProperties;
			return this;
		}

		@JsonProperty("database")
		public Builder setDatabaseName(String databaseName) {
			this.databaseName = databaseName;
			return this;
		}

		@JsonProperty("measurement")
		public Builder setMeasurementName(String measurementName) {
			this.measurementName = measurementName;
			return this;
		}

		@JsonProperty("status_retention_policies")
		@JsonDeserialize(as = ArrayList.class)
		public Builder setFrequentStatusRetentionPolicies(Collection<FrequentObject<RetentionPolicySetting>> frequentStatusRetentionPolicies) {
			this.frequentStatusRetentionPolicies = frequentStatusRetentionPolicies;
			return this;
		}

		@JsonProperty("event_retention_policy")
		public Builder setEventRetentionPolicySetting(RetentionPolicySetting eventRetentionPolicySetting) {
			this.eventRetentionPolicySetting = eventRetentionPolicySetting;
			return this;
		}

		public InfluxDbDatabaseSettings build() {
			return new InfluxDbDatabaseSettings(influxProperties, okHttpProperties, databaseName, measurementName, frequentStatusRetentionPolicies, eventRetentionPolicySetting);
		}
	}
}

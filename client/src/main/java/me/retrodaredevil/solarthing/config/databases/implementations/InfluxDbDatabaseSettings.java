package me.retrodaredevil.solarthing.config.databases.implementations;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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
public final class InfluxDbDatabaseSettings implements DatabaseSettings {
	public static final DatabaseType TYPE = new SimpleDatabaseType("influxdb");

	@JsonUnwrapped
	private final InfluxProperties influxProperties;
	@JsonUnwrapped
	private final OkHttpProperties okHttpProperties;
	@JsonProperty("database")
	private final String databaseName;
	@JsonProperty("measurement")
	private final String measurementName;
	@JsonProperty("retention_policies")
	@JsonDeserialize(as = ArrayList.class)
	private final List<FrequentObject<RetentionPolicySetting>> frequentRetentionPolicyList;

	private InfluxDbDatabaseSettings(){
		// Constructor that Jackson calls when deserializing
		influxProperties = null;
		okHttpProperties = null;
		databaseName = null;
		measurementName = null;
		frequentRetentionPolicyList = null;
	}

	public InfluxDbDatabaseSettings(InfluxProperties influxProperties, OkHttpProperties okHttpProperties, String databaseName, String measurementName, Collection<FrequentObject<RetentionPolicySetting>> frequentRetentionPolicies) {
		this.influxProperties = requireNonNull(influxProperties);
		this.okHttpProperties = requireNonNull(okHttpProperties);
		this.databaseName = databaseName;
		this.measurementName = measurementName;
		this.frequentRetentionPolicyList = Collections.unmodifiableList(new ArrayList<>(frequentRetentionPolicies));
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

	public List<FrequentObject<RetentionPolicySetting>> getFrequentRetentionPolicyList() {
		return frequentRetentionPolicyList;
	}
}

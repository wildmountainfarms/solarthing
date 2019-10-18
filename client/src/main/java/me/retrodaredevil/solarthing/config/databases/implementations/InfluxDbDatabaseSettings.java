package me.retrodaredevil.solarthing.config.databases.implementations;

import me.retrodaredevil.influxdb.InfluxProperties;
import me.retrodaredevil.okhttp3.OkHttpProperties;
import me.retrodaredevil.solarthing.config.databases.DatabaseSettings;
import me.retrodaredevil.solarthing.config.databases.DatabaseType;
import me.retrodaredevil.solarthing.influxdb.retention.RetentionPolicySetting;
import me.retrodaredevil.solarthing.util.frequency.FrequentObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.requireNonNull;

public final class InfluxDbDatabaseSettings implements DatabaseSettings {
	public static final DatabaseType TYPE = new DatabaseType() { };

	private final InfluxProperties influxProperties;
	private final OkHttpProperties okHttpProperties;
	private final String databaseName;
	private final String measurementName;
	private final List<FrequentObject<RetentionPolicySetting>> frequentRetentionPolicyList;

	public InfluxDbDatabaseSettings(InfluxProperties influxProperties, OkHttpProperties okHttpProperties, String databaseName, String measurementName, Collection<FrequentObject<RetentionPolicySetting>> frequentRetentionPolicies) {
		this.influxProperties = requireNonNull(influxProperties);
		this.okHttpProperties = requireNonNull(okHttpProperties);
		this.databaseName = databaseName;
		this.measurementName = measurementName;
		this.frequentRetentionPolicyList = Collections.unmodifiableList(new ArrayList<>(frequentRetentionPolicies));
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

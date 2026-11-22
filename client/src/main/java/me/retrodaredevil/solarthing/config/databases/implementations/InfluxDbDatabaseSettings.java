package me.retrodaredevil.solarthing.config.databases.implementations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import me.retrodaredevil.influxdb.influxdb1.InfluxProperties;
import me.retrodaredevil.okhttp3.OkHttpProperties;
import me.retrodaredevil.solarthing.config.databases.DatabaseSettings;
import me.retrodaredevil.solarthing.config.databases.DatabaseType;
import me.retrodaredevil.solarthing.config.databases.SimpleDatabaseType;
import me.retrodaredevil.solarthing.influxdb.retention.RetentionPolicySetting;
import me.retrodaredevil.solarthing.util.frequency.FrequentObject;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;

import static java.util.Objects.requireNonNull;

@JsonTypeName("influxdb")
@NullMarked
public final class InfluxDbDatabaseSettings implements DatabaseSettings {
	public static final DatabaseType TYPE = new SimpleDatabaseType("influxdb");

	private final InfluxProperties influxProperties;
	private final OkHttpProperties okHttpProperties;
	private final @Nullable String databaseName;
	private final @Nullable String measurementName;
	private final List<FrequentObject<RetentionPolicySetting>> frequentStatusRetentionPolicyList;

	private final @Nullable RetentionPolicySetting eventRetentionPolicySetting;

	// Note that PROPERTIES can be inferred here, but for consistency when using @JsonUnwrapped we define the mode explicitly
	@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
	public InfluxDbDatabaseSettings(
			@JsonUnwrapped InfluxProperties influxProperties,
			@JsonUnwrapped OkHttpProperties okHttpProperties,
			@JsonProperty("database") @Nullable String databaseName,
			@JsonProperty("measurement") @Nullable String measurementName,
			@JsonProperty("status_retention_policies") Collection<FrequentObject<RetentionPolicySetting>> frequentRetentionPolicies,
			@JsonProperty("event_retention_policy") @Nullable RetentionPolicySetting eventRetentionPolicySetting) {
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
}

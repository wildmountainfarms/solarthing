package me.retrodaredevil.solarthing.config.databases.implementations;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import me.retrodaredevil.influxdb.influxdb2.InfluxDb2Properties;
import me.retrodaredevil.okhttp3.OkHttpProperties;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.config.databases.DatabaseSettings;
import me.retrodaredevil.solarthing.config.databases.DatabaseType;
import me.retrodaredevil.solarthing.config.databases.SimpleDatabaseType;

import static java.util.Objects.requireNonNull;

@JsonDeserialize(builder = InfluxDb2DatabaseSettings.Builder.class)
@JsonTypeName("influxdb2")
public class InfluxDb2DatabaseSettings implements DatabaseSettings {
	public static final DatabaseType TYPE = new SimpleDatabaseType("influxdb2");

	private final InfluxDb2Properties influxDbProperties;
	private final OkHttpProperties okHttpProperties;
	private final String bucketName;
	private final String measurementName;

	public InfluxDb2DatabaseSettings(InfluxDb2Properties influxDbProperties, OkHttpProperties okHttpProperties, String bucketName, String measurementName) {
		requireNonNull(this.influxDbProperties = influxDbProperties);
		requireNonNull(this.okHttpProperties = okHttpProperties);
		this.bucketName = bucketName;
		this.measurementName = measurementName;
	}

	@Override
	public DatabaseType getDatabaseType() {
		return TYPE;
	}

	public InfluxDb2Properties getInfluxDbProperties() {
		return influxDbProperties;
	}
	public OkHttpProperties getOkHttpProperties() {
		return okHttpProperties;
	}

	public @Nullable String getBucketName() {
		return bucketName;
	}

	public @Nullable String getMeasurementName() {
		return measurementName;
	}

	@JsonPOJOBuilder
	public static class Builder {
		private InfluxDb2Properties influxDbProperties;
		private OkHttpProperties okHttpProperties;
		private String bucketName;
		private String measurementName;

		public InfluxDb2DatabaseSettings build() {
			return new InfluxDb2DatabaseSettings(influxDbProperties, okHttpProperties, bucketName, measurementName);
		}

		@JsonUnwrapped
		public Builder setInfluxDbProperties(InfluxDb2Properties influxDbProperties) {
			this.influxDbProperties = influxDbProperties;
			return this;
		}

		@JsonUnwrapped
		public Builder setOkHttpProperties(OkHttpProperties okHttpProperties) {
			this.okHttpProperties = okHttpProperties;
			return this;
		}

		@JsonProperty("bucket")
		public Builder setBucketName(String bucketName) {
			this.bucketName = bucketName;
			return this;
		}

		@JsonProperty("measurement")
		public Builder setMeasurementName(String measurementName) {
			this.measurementName = measurementName;
			return this;
		}

	}
}

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

	public InfluxDb2DatabaseSettings(InfluxDb2Properties influxDbProperties, OkHttpProperties okHttpProperties) {
		requireNonNull(this.influxDbProperties = influxDbProperties);
		requireNonNull(this.okHttpProperties = okHttpProperties);
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

	@JsonPOJOBuilder
	public static class Builder {
		private InfluxDb2Properties influxDbProperties;
		private OkHttpProperties okHttpProperties;

		public InfluxDb2DatabaseSettings build() {
			return new InfluxDb2DatabaseSettings(influxDbProperties, okHttpProperties);
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

	}
}

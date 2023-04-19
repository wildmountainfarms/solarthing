package me.retrodaredevil.solarthing.config.databases.implementations;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.influxdb.influxdb2.InfluxDb2Properties;
import me.retrodaredevil.solarthing.jackson.UnwrappedDeserializer;
import me.retrodaredevil.okhttp3.OkHttpProperties;
import me.retrodaredevil.solarthing.config.databases.DatabaseSettings;
import me.retrodaredevil.solarthing.config.databases.DatabaseType;
import me.retrodaredevil.solarthing.config.databases.SimpleDatabaseType;

import static java.util.Objects.requireNonNull;

@JsonDeserialize(using = InfluxDb2DatabaseSettings.Deserializer.class)
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
	public String toString() {
		return "InfluxDB2 " + influxDbProperties.getUrl();
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
	static class Deserializer extends UnwrappedDeserializer<InfluxDb2DatabaseSettings, Builder> {
		Deserializer() {
			super(Builder.class, Builder::build);
		}
	}
	static class Builder {
		@JsonUnwrapped
		private InfluxDb2Properties influxDbProperties;
		@JsonUnwrapped
		private OkHttpProperties okHttpProperties;

		public InfluxDb2DatabaseSettings build() {
			return new InfluxDb2DatabaseSettings(influxDbProperties, okHttpProperties);
		}

	}
}

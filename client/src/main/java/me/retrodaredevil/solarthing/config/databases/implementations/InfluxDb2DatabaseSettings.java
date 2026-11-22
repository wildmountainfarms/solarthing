package me.retrodaredevil.solarthing.config.databases.implementations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import me.retrodaredevil.influxdb.influxdb2.InfluxDb2Properties;
import me.retrodaredevil.okhttp3.OkHttpProperties;
import me.retrodaredevil.solarthing.config.databases.DatabaseSettings;
import me.retrodaredevil.solarthing.config.databases.DatabaseType;
import me.retrodaredevil.solarthing.config.databases.SimpleDatabaseType;
import org.jspecify.annotations.NullMarked;

import static java.util.Objects.requireNonNull;

@JsonTypeName("influxdb2")
@NullMarked
public class InfluxDb2DatabaseSettings implements DatabaseSettings {
	public static final DatabaseType TYPE = new SimpleDatabaseType("influxdb2");

	private final InfluxDb2Properties influxDbProperties;
	private final OkHttpProperties okHttpProperties;

	@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
	public InfluxDb2DatabaseSettings(@JsonUnwrapped InfluxDb2Properties influxDbProperties, @JsonUnwrapped OkHttpProperties okHttpProperties) {
		this.influxDbProperties = requireNonNull(influxDbProperties);
		this.okHttpProperties = requireNonNull(okHttpProperties);
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
}

package me.retrodaredevil.influxdb.influxdb2;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;

public interface InfluxDb2Properties {
	/**
	 * @return The Url of the database. This looks something like http://localhost:8086
	 */
	@JsonProperty("url")
	@NotNull String getUrl();

	@JsonProperty("token")
	@Nullable char[] getToken();

	@Nullable String getUsername();
	@Nullable char[] getPassword();

	@NotNull String getOrg();
}

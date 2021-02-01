package me.retrodaredevil.influxdb.influxdb2;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;

@JsonDeserialize(as = ImmutableInfluxDb2Properties.class)
public interface InfluxDb2Properties {
	/**
	 * @return The Url of the database. This looks something like http://localhost:8086
	 */
	@JsonProperty("url")
	@NotNull String getUrl();

	@JsonProperty("token")
	@Nullable char[] getToken();

	@JsonProperty("username")
	@Nullable String getUsername();
	@JsonProperty("password")
	@Nullable char[] getPassword();

	@JsonProperty("org")
	@NotNull String getOrg();

	static InfluxDb2Properties createTokenAuth(String url, char[] token, String org) {
		return new ImmutableInfluxDb2Properties(url, token, null, null, org);
	}
	static InfluxDb2Properties createUserAuth(String url, String username, char[] password, String org) {
		return new ImmutableInfluxDb2Properties(url, null, username, password, org);
	}
}

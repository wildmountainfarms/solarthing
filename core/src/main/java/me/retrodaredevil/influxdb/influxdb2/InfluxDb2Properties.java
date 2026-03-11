package me.retrodaredevil.influxdb.influxdb2;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@JsonDeserialize(as = ImmutableInfluxDb2Properties.class)
public interface InfluxDb2Properties {
	/**
	 * @return The Url of the database. This looks something like http://localhost:8086
	 */
	@JsonProperty("url")
	@NonNull String getUrl();

	@JsonProperty("token")
	@Nullable char[] getToken();

	@JsonProperty("username")
	@Nullable String getUsername();
	@JsonProperty("password")
	@Nullable char[] getPassword();

	@JsonProperty("org")
	@NonNull String getOrg();

	static InfluxDb2Properties createTokenAuth(String url, char[] token, String org) {
		return new ImmutableInfluxDb2Properties(url, token, null, null, org);
	}
	static InfluxDb2Properties createUserAuth(String url, String username, char[] password, String org) {
		return new ImmutableInfluxDb2Properties(url, null, username, password, org);
	}
}

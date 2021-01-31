package me.retrodaredevil.influxdb.influxdb2;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;

public class ImmutableInfluxDb2Properties implements InfluxDb2Properties {
	private final String url;
	private final char[] token;
	private final String username;
	private final char[] password;
	private final String org;

	@JsonCreator
	ImmutableInfluxDb2Properties(
			@JsonProperty(value = "url", required = true) String url,
			@JsonProperty("token") char[] token,
			@JsonProperty("username") String username, @JsonProperty("password") char[] password,
			@JsonProperty(value = "org", required = true) String org) {
		this.url = url;
		this.token = token;
		this.username = username;
		this.password = password;
		this.org = org;
		if (token == null) {
			if (username == null || password == null) {
				throw new IllegalArgumentException("If you don't specify token, you must specify username and password!");
			}
		}
	}

	public ImmutableInfluxDb2Properties(InfluxDb2Properties properties) {
		this(properties.getUrl(), properties.getToken(), properties.getUsername(), properties.getPassword(), properties.getOrg());
	}


	@Override
	public @NotNull String getUrl() {
		return url;
	}

	@Override
	public @Nullable char[] getToken() {
		return token;
	}

	@Override
	public @Nullable String getUsername() {
		return username;
	}

	@Override
	public @Nullable char[] getPassword() {
		return password;
	}

	@Override
	public @NotNull String getOrg() {
		return org;
	}
}

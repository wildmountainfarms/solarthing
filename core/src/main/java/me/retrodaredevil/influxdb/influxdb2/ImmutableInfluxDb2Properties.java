package me.retrodaredevil.influxdb.influxdb2;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class ImmutableInfluxDb2Properties implements InfluxDb2Properties {
	private final String url;
	private final char @Nullable [] token;
	private final @Nullable String username;
	private final char @Nullable [] password;
	private final String org;

	@JsonCreator
	ImmutableInfluxDb2Properties(
			@JsonProperty(value = "url", required = true) String url,
			@JsonProperty("token") char @Nullable [] token,
			@JsonProperty("username") @Nullable String username, @JsonProperty("password") char @Nullable [] password,
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
	public String getUrl() {
		return url;
	}

	@Override
	public char @Nullable [] getToken() {
		return token;
	}

	@Override
	public @Nullable String getUsername() {
		return username;
	}

	@Override
	public char @Nullable [] getPassword() {
		return password;
	}

	@Override
	public String getOrg() {
		return org;
	}
}

package me.retrodaredevil.influxdb.influxdb1;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import static java.util.Objects.requireNonNull;

@JsonPOJOBuilder
public class InfluxPropertiesBuilder implements InfluxProperties {
	private String url = "http://localhost:8086";
	private String username = "root";
	private String password = "root";

	public InfluxProperties build(){
		return new ImmutableInfluxProperties(this);
	}

	@Override
	public String getUrl() {
		return url;
	}
	@JsonSetter("url")
	public InfluxPropertiesBuilder setUrl(String url){
		this.url = requireNonNull(url);
		return this;
	}

	@Override
	public String getUsername() {
		return username;
	}
	@JsonSetter("username")
	public InfluxPropertiesBuilder setUsername(String username){
		this.username = requireNonNull(username);
		return this;
	}

	@Override
	public String getPassword() {
		return password;
	}
	@JsonSetter("password")
	public InfluxPropertiesBuilder setPassword(String password){
		this.password = requireNonNull(password);
		return this;
	}
}

package me.retrodaredevil.influxdb;

import static java.util.Objects.requireNonNull;

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
	public InfluxPropertiesBuilder setUrl(String url){
		this.url = requireNonNull(url);
		return this;
	}

	@Override
	public String getUsername() {
		return username;
	}
	public InfluxPropertiesBuilder setUsername(String username){
		this.username = requireNonNull(username);
		return this;
	}

	@Override
	public String getPassword() {
		return password;
	}
	public InfluxPropertiesBuilder setPassword(String password){
		this.password = requireNonNull(password);
		return this;
	}
}

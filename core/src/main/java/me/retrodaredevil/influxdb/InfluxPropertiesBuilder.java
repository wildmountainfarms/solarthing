package me.retrodaredevil.influxdb;

import static java.util.Objects.requireNonNull;

public class InfluxPropertiesBuilder implements InfluxProperties {
	private String url = "http://localhost:8086";

	private String username = "root";

	private String password = "root";

	private boolean isRetryOnConnectionFailure = true;
	private int callTimeoutMillis = 0;
	private int connectTimeoutMillis = 10_000;
	private int readTimeoutMillis = 10_000;
	private int writeTimeoutMillis = 10_000;
	private int pingIntervalMillis = 0;

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

	@Override
	public boolean isRetryOnConnectionFailure() {
		return isRetryOnConnectionFailure;
	}
	public InfluxPropertiesBuilder setRetryOnConnectionFailure(boolean isRetryOnConnectionFailure){
		this.isRetryOnConnectionFailure = isRetryOnConnectionFailure;
		return this;
	}

	@Override
	public int getCallTimeoutMillis() {
		return callTimeoutMillis;
	}
	public InfluxPropertiesBuilder setCallTimeoutMillis(int callTimeoutMillis){
		this.callTimeoutMillis = callTimeoutMillis;
		return this;
	}

	@Override
	public int getConnectTimeoutMillis() {
		return connectTimeoutMillis;
	}
	public InfluxPropertiesBuilder setConnectTimeoutMillis(int connectTimeoutMillis){
		this.connectTimeoutMillis = connectTimeoutMillis;
		return this;
	}

	@Override
	public int getReadTimeoutMillis() {
		return readTimeoutMillis;
	}
	public InfluxPropertiesBuilder setReadTimeoutMillis(int readTimeoutMillis){
		this.readTimeoutMillis = readTimeoutMillis;
		return this;
	}

	@Override
	public int getWriteTimeoutMillis() {
		return writeTimeoutMillis;
	}
	public InfluxPropertiesBuilder setWriteTimeoutMillis(int writeTimeoutMillis){
		this.writeTimeoutMillis = writeTimeoutMillis;
		return this;
	}

	@Override
	public int getPingIntervalMillis() {
		return pingIntervalMillis;
	}
	public InfluxPropertiesBuilder setPingIntervalMillis(int pingIntervalMillis){
		this.pingIntervalMillis = pingIntervalMillis;
		return this;
	}

}

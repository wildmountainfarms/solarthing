package me.retrodaredevil.influxdb;

public class ImmutableInfluxProperties implements InfluxProperties {
	private final String url;

	private final String username;

	private final String password;

	private final boolean isRetryOnConnectionFailure;
	private final int callTimeoutMillis;
	private final int connectTimeoutMillis;
	private final int readTimeoutMillis;
	private final int writeTimeoutMillis;
	private final int pingIntervalMillis;

	public ImmutableInfluxProperties(InfluxProperties influxProperties){
		url = influxProperties.getUrl();
		username = influxProperties.getUsername();
		password = influxProperties.getPassword();
		isRetryOnConnectionFailure = influxProperties.isRetryOnConnectionFailure();
		callTimeoutMillis = influxProperties.getCallTimeoutMillis();
		connectTimeoutMillis = influxProperties.getConnectTimeoutMillis();
		readTimeoutMillis = influxProperties.getReadTimeoutMillis();
		writeTimeoutMillis = influxProperties.getWriteTimeoutMillis();
		pingIntervalMillis = influxProperties.getPingIntervalMillis();
	}

	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public boolean isRetryOnConnectionFailure() {
		return isRetryOnConnectionFailure;
	}

	@Override
	public int getCallTimeoutMillis() {
		return callTimeoutMillis;
	}

	@Override
	public int getConnectTimeoutMillis() {
		return connectTimeoutMillis;
	}

	@Override
	public int getReadTimeoutMillis() {
		return readTimeoutMillis;
	}

	@Override
	public int getWriteTimeoutMillis() {
		return writeTimeoutMillis;
	}

	@Override
	public int getPingIntervalMillis() {
		return pingIntervalMillis;
	}
}

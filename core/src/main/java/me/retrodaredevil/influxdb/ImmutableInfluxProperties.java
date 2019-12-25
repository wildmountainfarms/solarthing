package me.retrodaredevil.influxdb;

public class ImmutableInfluxProperties implements InfluxProperties {
	private final String url;
	private final String username;
	private final String password;

	public ImmutableInfluxProperties(InfluxProperties influxProperties){
		url = influxProperties.getUrl();
		username = influxProperties.getUsername();
		password = influxProperties.getPassword();
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

}

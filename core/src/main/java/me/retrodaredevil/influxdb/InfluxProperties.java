package me.retrodaredevil.influxdb;

public interface InfluxProperties {
	/**
	 * @return The Url of the database. This looks something like http://localhost:8086
	 */
	String getUrl();

	/**
	 * @return The username
	 */
	String getUsername();

	/**
	 * @return The password
	 */
	String getPassword();

}

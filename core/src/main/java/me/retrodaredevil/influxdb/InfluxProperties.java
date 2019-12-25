package me.retrodaredevil.influxdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties
public interface InfluxProperties {
	/**
	 * @return The Url of the database. This looks something like http://localhost:8086
	 */
	@JsonProperty("url")
	String getUrl();

	/**
	 * @return The username
	 */
	@JsonProperty("username")
	String getUsername();

	/**
	 * @return The password
	 */
	@JsonProperty("password")
	String getPassword();

}

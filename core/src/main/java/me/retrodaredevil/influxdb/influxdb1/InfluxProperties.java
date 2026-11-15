package me.retrodaredevil.influxdb.influxdb1;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import org.jspecify.annotations.NullMarked;
import tools.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = ImmutableInfluxProperties.class)
@JsonExplicit
@NullMarked
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

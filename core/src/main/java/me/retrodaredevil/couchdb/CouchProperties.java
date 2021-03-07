package me.retrodaredevil.couchdb;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;

@JsonDeserialize(as = ImmutableCouchProperties.class)
@JsonExplicit
public interface CouchProperties {
	@JsonProperty("protocol")
	@NotNull String getProtocol();

	@JsonProperty("host")
	@NotNull String getHost();

	@JsonProperty("path")
	@Nullable String getPath();

	@JsonProperty("port")
	int getPort();

	@JsonProperty("username")
	@Nullable String getUsername();

	@JsonProperty("password")
	@Nullable String getPassword();

	/**
	 * @return The socket timeout in ms
	 */
	@Nullable Integer getSocketTimeoutMillis();
	/**
	 * @return The connection timeout in ms
	 */
	@Nullable Integer getConnectionTimeoutMillis();

	@JsonProperty("max_connections")
	@Nullable Integer getMaxConnections();

	@JsonProperty("proxy_host")
	@Nullable String getProxyHost();

	@JsonProperty("proxy_port")
	@Nullable Integer getProxyPort();

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("socket_timeout")
	default @Nullable Float getSocketTimeoutSeconds() {
		Integer millis = getSocketTimeoutMillis();
		return millis == null ? null : millis / 1000.0f;
	}
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("connection_timeout")
	default @Nullable Float getConnectionTimeoutSeconds() {
		Integer millis = getConnectionTimeoutMillis();
		return millis == null ? null : millis / 1000.0f;
	}
}

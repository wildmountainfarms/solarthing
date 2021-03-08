package me.retrodaredevil.okhttp3;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;

@JsonDeserialize(builder = OkHttpPropertiesBuilder.class)
@JsonExplicit
public interface OkHttpProperties {
	@JsonProperty("retry_on_connection_failure")
	boolean isRetryOnConnectionFailure(); // = true;
	int getCallTimeoutMillis(); // = 0;
	int getConnectTimeoutMillis();// = 10_000;
	int getReadTimeoutMillis(); // = 10_000;
	int getWriteTimeoutMillis();// = 10_000;

	/**
	 * From OkHttp documentation:
	 * <p>
	 * Configure the protocols used by this client to communicate with remote servers.
	 * By default this client will prefer the most efficient transport available, falling back to more ubiquitous protocols.
	 * Applications should only call this method to avoid specific compatibility problems,
	 * such as web servers that behave incorrectly when HTTP/2 is enabled.
	 * @return The ping interval in milliseconds
	 */
	int getPingIntervalMillis();// = 0;

	@JsonProperty("call_timeout")
	default float getCallTimeoutSeconds(){ return getCallTimeoutMillis() / 1000f; }
	@JsonProperty("connection_timeout")
	default float getConnectTimeoutSeconds() { return getConnectTimeoutMillis() / 1000f; }
	@JsonProperty("read_timeout")
	default float getReadTimeoutSeconds() { return getReadTimeoutMillis() / 1000f; }
	@JsonProperty("write_timeout")
	default float getWriteTimeoutSeconds() { return getWriteTimeoutMillis() / 1000f; }

	@JsonProperty("ping_interval")
	default float getPingIntervalSeconds(){ return getPingIntervalMillis() / 1000f; }
}

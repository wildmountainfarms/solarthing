package me.retrodaredevil.okhttp3;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = OkHttpPropertiesBuilder.class)
public interface OkHttpProperties {
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
}

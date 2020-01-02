package me.retrodaredevil.couchdb;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = ImmutableCouchProperties.class)
public interface CouchProperties {
	String getProtocol();
	String getHost();
	String getPath();
	int getPort();

	String getUsername();
	String getPassword();

	// TODO make default values for some of these 'nullable' instead of 0
	/**
	 * @return The socket timeout in ms
	 */
	int getSocketTimeoutMillis();
	/**
	 * @return The connection timeout in ms
	 */
	int getConnectionTimeoutMillis();
	int getMaxConnections();
	String getProxyHost();
	int getProxyPort();
}

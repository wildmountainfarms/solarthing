package me.retrodaredevil.couchdb;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = ImmutableCouchProperties.class)
public interface CouchProperties {
	String getDatabase();
	boolean isCreateIfNotExist();
	String getProtocol();
	String getHost();
	String getPath();
	int getPort();
	
	String getUsername();
	String getPassword();
	
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

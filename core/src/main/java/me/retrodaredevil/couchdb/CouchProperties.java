package me.retrodaredevil.couchdb;

import org.lightcouch.CouchDbProperties;

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
	
	
	default CouchDbProperties createProperties(){
		return setProperties(new CouchDbProperties());
	}
	
	default CouchDbProperties setProperties(CouchDbProperties properties) {
		properties.clearPassword();
		return properties
			.setDbName(getDatabase())
			.setCreateDbIfNotExist(isCreateIfNotExist())
			.setProtocol(getProtocol())
			.setHost(getHost())
			.setPath(getPath())
			.setPort(getPort())
			.setUsername(getUsername())
			.setPassword(getPassword())
			.setSocketTimeout(getSocketTimeoutMillis())
			.setConnectionTimeout(getConnectionTimeoutMillis())
			.setMaxConnections(getMaxConnections())
			.setProxyHost(getProxyHost())
			.setProxyPort(getProxyPort())
			;
	}
}

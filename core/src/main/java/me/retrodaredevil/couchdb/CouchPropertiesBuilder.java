package me.retrodaredevil.couchdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public class CouchPropertiesBuilder {
	private String database;
	private boolean createIfNotExist;
	private String protocol;
	private String host;
	private String path = null;
	private int port;
	private String username;
	private String password;
	private int socketTimeout = 0;
	private int connectionTimeout = 0;
	private int maxConnections = 0;
	private String proxyHost = null;
	private int proxyPort = 0;
	
	public CouchPropertiesBuilder(){}
	
	public CouchPropertiesBuilder(String database, boolean createIfNotExist, String protocol, String host, int port, String username, String password) {
		this.database = database;
		this.createIfNotExist = createIfNotExist;
		this.protocol = protocol;
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
	}
	public CouchPropertiesBuilder(CouchProperties properties){
		database = properties.getDatabase();
		createIfNotExist = properties.isCreateIfNotExist();
		protocol = properties.getProtocol();
		host = properties.getHost();
		path = properties.getPath();
		port = properties.getPort();
		username = properties.getUsername();
		password = properties.getPassword();
		socketTimeout = properties.getSocketTimeoutMillis();
		connectionTimeout = properties.getConnectionTimeoutMillis();
		maxConnections = properties.getMaxConnections();
		proxyHost = properties.getProxyHost();
		proxyPort = properties.getProxyPort();
	}

	@JsonProperty("database")
	public CouchPropertiesBuilder setDatabase(String database) {
		this.database = database;
		return this;
	}
	
	public CouchPropertiesBuilder setCreateIfNotExist(boolean createIfNotExist) {
		this.createIfNotExist = createIfNotExist;
		return this;
	}

	@JsonSetter("protocol")
	public CouchPropertiesBuilder setProtocol(String protocol) {
		this.protocol = protocol;
		return this;
	}

	@JsonSetter("host")
	public CouchPropertiesBuilder setHost(String host) {
		this.host = host;
		return this;
	}

	@JsonSetter("path")
	public CouchPropertiesBuilder setPath(String path) {
		this.path = path;
		return this;
	}

	@JsonSetter("port")
	public CouchPropertiesBuilder setPort(int port) {
		this.port = port;
		return this;
	}

	@JsonSetter("username")
	public CouchPropertiesBuilder setUsername(String username) {
		this.username = username;
		return this;
	}

	@JsonSetter("password")
	public CouchPropertiesBuilder setPassword(String password) {
		this.password = password;
		return this;
	}
	
	/**
	 * @param socketTimeout Socket timeout in ms
	 */
	public CouchPropertiesBuilder setSocketTimeoutMillis(int socketTimeout) {
		this.socketTimeout = socketTimeout;
		return this;
	}
	@JsonSetter("socket_timeout")
	public CouchPropertiesBuilder setSocketTimeoutSeconds(float seconds) {
		this.socketTimeout = Math.round(seconds * 1000);
		return this;
	}
	
	/**
	 * @param connectionTimeout Connection timeout in ms
	 */
	public CouchPropertiesBuilder setConnectionTimeoutMillis(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
		return this;
	}
	@JsonSetter("connection_timeout")
	public CouchPropertiesBuilder setConnectionTimeoutSeconds(float seconds) {
		this.connectionTimeout = Math.round(seconds * 1000);
		return this;
	}

	@JsonSetter("max_connections")
	public CouchPropertiesBuilder setMaxConnections(int maxConnections) {
		this.maxConnections = maxConnections;
		return this;
	}

	@JsonSetter("proxy_host")
	public CouchPropertiesBuilder setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
		return this;
	}

	@JsonSetter("proxy_port")
	public CouchPropertiesBuilder setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
		return this;
	}
	
	public CouchProperties build() {
		return new ImmutableCouchProperties(database, createIfNotExist, protocol, host, path, port, username, password, socketTimeout, connectionTimeout, maxConnections, proxyHost, proxyPort);
	}
}

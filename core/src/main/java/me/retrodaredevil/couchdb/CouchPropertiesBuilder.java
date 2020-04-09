package me.retrodaredevil.couchdb;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@SuppressWarnings({"UnusedReturnValue", "unused"})
@JsonPOJOBuilder
public class CouchPropertiesBuilder {
	private String protocol;
	private String host;
	private String path = null;
	private int port;
	private String username;
	private String password;
	private Integer socketTimeout = null;
	private Integer connectionTimeout = null;
	private Integer maxConnections = null;
	private String proxyHost = null;
	private Integer proxyPort = null;

	public CouchPropertiesBuilder(){}

	public CouchPropertiesBuilder(String protocol, String host, int port, String username, String password) {
		this.protocol = protocol;
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
	}
	public CouchPropertiesBuilder(CouchProperties properties){
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
	public CouchPropertiesBuilder setSocketTimeoutMillis(Integer socketTimeout) {
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
	public CouchPropertiesBuilder setConnectionTimeoutMillis(Integer connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
		return this;
	}
	@JsonSetter("connection_timeout")
	public CouchPropertiesBuilder setConnectionTimeoutSeconds(float seconds) {
		this.connectionTimeout = Math.round(seconds * 1000);
		return this;
	}

	@JsonSetter("max_connections")
	public CouchPropertiesBuilder setMaxConnections(Integer maxConnections) {
		this.maxConnections = maxConnections;
		return this;
	}

	@JsonSetter("proxy_host")
	public CouchPropertiesBuilder setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
		return this;
	}

	@JsonSetter("proxy_port")
	public CouchPropertiesBuilder setProxyPort(Integer proxyPort) {
		this.proxyPort = proxyPort;
		return this;
	}

	public CouchProperties build() {
		return new ImmutableCouchProperties(protocol, host, path, port, username, password, socketTimeout, connectionTimeout, maxConnections, proxyHost, proxyPort);
	}
}

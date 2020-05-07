package me.retrodaredevil.couchdb;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.NotNull;

import static java.util.Objects.requireNonNull;

@JsonDeserialize(builder = CouchPropertiesBuilder.class)
class ImmutableCouchProperties implements CouchProperties {
	private final String protocol, host, path;
	private final int port;
	private final String username, password;
	private final Integer socketTimeout, connectionTimeout, maxConnections;
	private final String proxyHost;
	private final Integer proxyPort;

	ImmutableCouchProperties(String protocol, String host, String path, int port, String username, String password, Integer socketTimeout, Integer connectionTimeout, Integer maxConnections, String proxyHost, Integer proxyPort) {
		this.protocol = requireNonNull(protocol);
		this.host = requireNonNull(host);
		this.path = path;
		this.port = port;
		this.username = username;
		this.password = password;
		this.socketTimeout = socketTimeout;
		this.connectionTimeout = connectionTimeout;
		this.maxConnections = maxConnections;
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
	}
	@NotNull
	@Override public String getProtocol() { return protocol; }

	@NotNull
	@Override public String getHost() { return host; }

	@Override public String getPath() { return path; }

	@Override public int getPort() { return port; }

	@Override public String getUsername() { return username; }

	@Override public String getPassword() { return password; }

	@Override public Integer getSocketTimeoutMillis() { return socketTimeout; }

	@Override public Integer getConnectionTimeoutMillis() { return connectionTimeout; }

	@Override public Integer getMaxConnections() { return maxConnections; }

	@Override public String getProxyHost() { return proxyHost; }

	@Override public Integer getProxyPort() { return proxyPort; }
}

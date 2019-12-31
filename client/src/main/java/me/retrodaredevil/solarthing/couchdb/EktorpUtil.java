package me.retrodaredevil.solarthing.couchdb;

import me.retrodaredevil.couchdb.CouchProperties;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;

import java.net.MalformedURLException;

public final class EktorpUtil {
	private EktorpUtil(){ throw new UnsupportedOperationException(); }
	public static HttpClient createHttpClient(CouchProperties properties){
		int maxConnections = properties.getMaxConnections();
		int proxyPort = properties.getProxyPort();
		int connectionTimeout = properties.getConnectionTimeoutMillis();
		int socketTimeout = properties.getSocketTimeoutMillis();
		try {
			String rawPath = properties.getPath();
			String path = rawPath == null ? "" : rawPath;
			StdHttpClient.Builder builder = new StdHttpClient.Builder()
					.url(properties.getProtocol() + "://" + properties.getHost() + ":" + properties.getPort() + path)
					.username(properties.getUsername()) // may be null
					.password(properties.getPassword()) // may be null
					.proxy(properties.getProxyHost()) // may be null
					;
			if(connectionTimeout != 0){
				builder.connectionTimeout(connectionTimeout);
			}
			if(socketTimeout != 0){
				builder.socketTimeout(socketTimeout);
			}
			if(maxConnections != 0){
				builder.maxConnections(maxConnections);
			}
			if(proxyPort != 0){
				builder.proxyPort(proxyPort);
			}
			return builder.build();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
}

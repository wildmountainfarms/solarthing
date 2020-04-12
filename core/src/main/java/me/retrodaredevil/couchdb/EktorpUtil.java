package me.retrodaredevil.couchdb;

import me.retrodaredevil.couchdb.CouchProperties;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;

import java.net.MalformedURLException;

public final class EktorpUtil {
	private EktorpUtil(){ throw new UnsupportedOperationException(); }
	public static HttpClient createHttpClient(CouchProperties properties){
		Integer maxConnections = properties.getMaxConnections();
		Integer proxyPort = properties.getProxyPort();
		Integer connectionTimeout = properties.getConnectionTimeoutMillis();
		Integer socketTimeout = properties.getSocketTimeoutMillis();
		String rawPath = properties.getPath();
		String path = rawPath == null ? "" : rawPath;
		try {
			StdHttpClient.Builder builder = new StdHttpClient.Builder()
					.url(properties.getProtocol() + "://" + properties.getHost() + ":" + properties.getPort() + path)
					.username(properties.getUsername()) // may be null
					.password(properties.getPassword()) // may be null
					.proxy(properties.getProxyHost()) // may be null
					;
			if(connectionTimeout != null){
				builder.connectionTimeout(connectionTimeout);
			}
			if(socketTimeout != null){
				builder.socketTimeout(socketTimeout);
			}
			if(maxConnections != null){
				builder.maxConnections(maxConnections);
			}
			if(proxyPort != null){
				builder.proxyPort(proxyPort);
			}
			return builder.build();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
}

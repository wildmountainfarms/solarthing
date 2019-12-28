package me.retrodaredevil.solarthing.couchdb;

import me.retrodaredevil.couchdb.CouchProperties;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;

import java.net.MalformedURLException;

public final class EktorpUtil {
	private EktorpUtil(){ throw new UnsupportedOperationException(); }
	public static HttpClient createHttpClient(CouchProperties properties){
		try {
			return new StdHttpClient.Builder()
					.url(properties.getProtocol() + "://" + properties.getHost())
					.port(properties.getPort())
					.socketTimeout(properties.getSocketTimeoutMillis())
					.connectionTimeout(properties.getConnectionTimeoutMillis())
					.username(properties.getUsername())
					.password(properties.getPassword())
					.proxyPort(properties.getProxyPort())
					.maxConnections(properties.getMaxConnections())
					.build();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
}

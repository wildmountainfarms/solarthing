package me.retrodaredevil.couchdb;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import okhttp3.HttpUrl;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@SuppressWarnings({"UnusedReturnValue", "unused"})
@JsonPOJOBuilder
@NullMarked
public class CouchPropertiesBuilder {
	private HttpUrl.Builder httpUrlBuilder;
	private @Nullable String username;
	private @Nullable String password;
	private boolean basicAuth = false;

	public CouchPropertiesBuilder(){
		httpUrlBuilder = new HttpUrl.Builder();
	}

	public CouchPropertiesBuilder(String protocol, String host, int port, @Nullable String username, @Nullable String password) {
		httpUrlBuilder = new HttpUrl.Builder()
				.scheme(protocol)
				.host(host)
				.port(port);
		this.username = username;
		this.password = password;
	}
	public CouchPropertiesBuilder(HttpUrl url, @Nullable String username, @Nullable String password) {
		httpUrlBuilder = url.newBuilder();
		this.username = username;
		this.password = password;
	}
	public CouchPropertiesBuilder(CouchProperties properties){
		httpUrlBuilder = properties.getHttpUrl().newBuilder();
		username = properties.getUsername();
		password = properties.getPassword();
	}
	public CouchPropertiesBuilder setHttpUrl(HttpUrl url) {
		httpUrlBuilder = url.newBuilder();
		return this;
	}
	@JsonSetter("url")
	public CouchPropertiesBuilder setUrl(String url) {
		return setHttpUrl(HttpUrl.get(url));
	}

	@JsonSetter("protocol")
	public CouchPropertiesBuilder setProtocol(String protocol) {
		httpUrlBuilder.scheme(protocol);
		return this;
	}

	@JsonSetter("host")
	public CouchPropertiesBuilder setHost(String host) {
		httpUrlBuilder.host(host);
		return this;
	}

	@JsonSetter("path")
	public CouchPropertiesBuilder setPath(String path) {
		httpUrlBuilder.addEncodedPathSegments(path);
		return this;
	}

	@JsonSetter("port")
	public CouchPropertiesBuilder setPort(int port) {
		httpUrlBuilder.port(port);
		return this;
	}

	@JsonSetter("username")
	public CouchPropertiesBuilder setUsername(@Nullable String username) {
		this.username = username;
		return this;
	}

	@JsonSetter("password")
	public CouchPropertiesBuilder setPassword(@Nullable String password) {
		this.password = password;
		return this;
	}

	@JsonSetter("basic_auth")
	public CouchPropertiesBuilder setBasicAuth(boolean basicAuth) {
		this.basicAuth = basicAuth;
		return this;
	}

	public CouchProperties build() {
		return new ImmutableCouchProperties(httpUrlBuilder.build(), username, password, basicAuth);
	}
}

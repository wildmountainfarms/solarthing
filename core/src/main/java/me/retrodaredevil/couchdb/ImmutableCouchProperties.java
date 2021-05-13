package me.retrodaredevil.couchdb;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.NotNull;
import okhttp3.HttpUrl;

import static java.util.Objects.requireNonNull;

@JsonDeserialize(builder = CouchPropertiesBuilder.class)
class ImmutableCouchProperties implements CouchProperties {
	private final HttpUrl url;
	private final String username, password;
	private final boolean basicAuth;

	ImmutableCouchProperties(HttpUrl url, String username, String password, boolean basicAuth) {
		this.basicAuth = basicAuth;
		requireNonNull(this.url = url);
		this.username = username;
		this.password = password;
	}

	@Override
	public @NotNull HttpUrl getHttpUrl() {
		return url;
	}

	@Override
	public @NotNull String getUrl() {
		return url.toString();
	}

	@NotNull
	@Override public String getProtocol() { return url.scheme(); }

	@NotNull
	@Override public String getHost() { return url.host(); }

	@Override public String getPath() { return url.encodedPath(); }

	@Override public int getPort() { return url.port(); }

	@Override public String getUsername() { return username; }

	@Override public String getPassword() { return password; }

	@Override
	public boolean useBasicAuth() { return basicAuth; }
}

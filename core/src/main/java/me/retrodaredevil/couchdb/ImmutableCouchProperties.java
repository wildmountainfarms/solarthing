package me.retrodaredevil.couchdb;

import okhttp3.HttpUrl;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import tools.jackson.databind.annotation.JsonDeserialize;

import java.net.URI;

import static java.util.Objects.requireNonNull;

@JsonDeserialize(builder = CouchPropertiesBuilder.class)
@NullMarked
class ImmutableCouchProperties implements CouchProperties {
	private final HttpUrl url;
	private final @Nullable String username, password;
	private final boolean basicAuth;

	ImmutableCouchProperties(HttpUrl url, @Nullable String username, @Nullable String password, boolean basicAuth) {
		this.basicAuth = basicAuth;
		this.url = requireNonNull(url);
		this.username = username;
		this.password = password;
	}

	@Override
	public HttpUrl getHttpUrl() {
		return url;
	}

	@Override
	public URI getUri() {
		return url.uri();
	}



	@Override public @Nullable String getUsername() { return username; }

	@Override public @Nullable String getPassword() { return password; }

	@Override
	public boolean forceBasicAuth() { return basicAuth; }

}

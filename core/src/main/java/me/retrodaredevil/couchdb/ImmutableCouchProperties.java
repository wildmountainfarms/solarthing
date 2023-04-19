package me.retrodaredevil.couchdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.NotNull;
import okhttp3.HttpUrl;

import java.net.URI;

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

	@JsonProperty("url") // JsonProperty to work with UnwrappedDeserializer
	@Override
	public @NotNull URI getUri() {
		return url.uri();
	}



	@JsonProperty("username") // JsonProperty to work with UnwrappedDeserializer
	@Override public String getUsername() { return username; }

	@JsonProperty("password") // JsonProperty to work with UnwrappedDeserializer
	@Override public String getPassword() { return password; }

	@JsonProperty("basic_auth") // JsonProperty to work with UnwrappedDeserializer
	@Override
	public boolean forceBasicAuth() { return basicAuth; }



	// methods below here should be removed eventually, but are here so that when
	//   UnwrappedDeserializer tries to serialize this it can see that these values exist
	// TODO find a better solution than using UnwrappedDeserializer
	@JsonProperty("protocol")
	private String getProtocol() { return url.scheme(); }
	@JsonProperty("host")
	private String getHost() { return url.host(); }
	@JsonProperty("path")
	private String getPath() { return url.encodedPath(); }
	@JsonProperty("port")
	private int getPort() { return url.port(); }
}

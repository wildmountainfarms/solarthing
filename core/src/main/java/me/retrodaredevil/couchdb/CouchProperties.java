package me.retrodaredevil.couchdb;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.couchdbjava.CouchDbAuth;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import okhttp3.HttpUrl;

import java.net.URI;

/**
 * Note: This does not support serialization officially.
 * Implementation may support serialization to make {@link me.retrodaredevil.solarthing.jackson.UnwrappedDeserializer} happy, but that's the only reason
 */
@JsonDeserialize(as = ImmutableCouchProperties.class)
@JsonExplicit
public interface CouchProperties {

	@NotNull HttpUrl getHttpUrl();
	@NotNull URI getUri();

	@Nullable String getUsername();
	@Nullable String getPassword();

	/**
	 * Indicates that basic auth is preferred instead of cookie authentication.
	 * This does not indicate whether authentication should be used!!! Whether {@link #getUsername()} is null determines if authentication is used.
	 */
	boolean forceBasicAuth();

	default CouchDbAuth getAuth() {
		String username = getUsername();
		if (username == null) {
			if (getPassword() != null) {
				throw new IllegalStateException("When password is defined you must also define username!");
			}
			return CouchDbAuth.createNoAuth();
		}
		String password = getPassword();
		if (password == null) {
			throw new IllegalStateException("Cannot have a defined username without a defined password!");
		}
		return CouchDbAuth.create(username, password);
	}
}

package me.retrodaredevil.couchdb;

import me.retrodaredevil.couchdbjava.CouchDbAuth;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import okhttp3.HttpUrl;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import tools.jackson.databind.annotation.JsonDeserialize;

import java.net.URI;

/**
 * Note: Implementations are not required to support serialization.
 */
@JsonDeserialize(as = ImmutableCouchProperties.class)
@JsonExplicit
@NullMarked
public interface CouchProperties {

	HttpUrl getHttpUrl();
	URI getUri();

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

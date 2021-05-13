package me.retrodaredevil.couchdbjava;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.Base64Variants;

import javax.annotation.CheckForNull;

import java.nio.charset.StandardCharsets;

import static java.util.Objects.requireNonNull;

public class CouchDbAuth {
	private static final CouchDbAuth NO_AUTH = new CouchDbAuth(null, null);

	private final String username;
	private final String password;

	private CouchDbAuth(String username, String password) {
		this.username = username;
		this.password = password;
	}
	public static CouchDbAuth create(String username, String password) {
		return new CouchDbAuth(requireNonNull(username), requireNonNull(password));
	}
	public static CouchDbAuth createNoAuth() {
		return NO_AUTH;
	}
	public boolean usesAuth() {
		return username != null;
	}
	// Use @CheckForNull because the user of this API can check usesAuth(), so they might already know if this is null or not

	public @CheckForNull String getUsername() {
		return username;
	}
	public @CheckForNull String getPassword() {
		return password;
	}

	public @CheckForNull String getBasicAuthString() {
		if (usesAuth()) {
			return Base64Variants.getDefaultVariant().encode((username + ":" + password).getBytes(StandardCharsets.UTF_8));
		}
		return null;
	}

}

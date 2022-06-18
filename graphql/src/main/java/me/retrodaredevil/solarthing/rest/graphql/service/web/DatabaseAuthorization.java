package me.retrodaredevil.solarthing.rest.graphql.service.web;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.GraphQLInclude;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;
import okhttp3.Cookie;
import okhttp3.HttpUrl;

import static java.util.Objects.requireNonNull;

/**
 * A class to encapsulate credentials stored on the frontend that are used to directly authorize with the database.
 * <p>
 * This can be serialized to JSON as well as deserialized from JSON
 * <p>
 * Although this is a concrete class, its internals may change if other components of SolarThing change.
 */
@JsonExplicit
public class DatabaseAuthorization {
	private final Cookie cookie;

	private DatabaseAuthorization(Cookie cookie) {
		this.cookie = cookie;
	}

	@JsonCreator
	public static DatabaseAuthorization create(
			@JsonProperty("url") @NotNull String urlString,
			@JsonProperty("cookie") @NotNull String cookieString
	) {

		// internally in Cookie.parse only HttpUrl.host() and HttpUrl.encodedPath() are used, so tacking a http:// on just allows us to easily use this
		HttpUrl url = HttpUrl.get("https://" + urlString);
		requireNonNull(url, "Parsing URL resulted in null! urlString: " + urlString);
		Cookie cookie = requireNonNull(Cookie.parse(url, cookieString), "Parsing cookie resulted in null! cookieString: " + cookieString + " urlString: " + urlString);
		return new DatabaseAuthorization(cookie);
	}
	public static DatabaseAuthorization create(Cookie cookie) {
		return new DatabaseAuthorization(cookie);
	}


	@JsonProperty("url") // public to avoid IllegalAccessException in GraphQL-spqr code
	public @NotNull String getUrlString() {
		return cookie.domain().toString();
	}

	@JsonProperty("cookie") // public to avoid IllegalAccessException in GraphQL-spqr code
	public @NotNull String getCookieString() {
		return cookie.toString();
	}

	@GraphQLInclude("expiresAt")
	public long getExpiresAt() {
		return cookie.expiresAt();
	}

	public Cookie getCookie() {
		return cookie;
	}
}

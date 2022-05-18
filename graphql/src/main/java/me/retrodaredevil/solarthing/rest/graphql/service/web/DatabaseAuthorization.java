package me.retrodaredevil.solarthing.rest.graphql.service.web;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
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
	public static DatabaseAuthorization create(@JsonProperty("url") String urlString, @JsonProperty("cookie") String cookieString) {

		HttpUrl url = HttpUrl.get(urlString);
		requireNonNull(url, "Parsing URL resulted in null! urlString: " + urlString);
		Cookie cookie = requireNonNull(Cookie.parse(url, cookieString), "Parsing cookie resulted in null! cookieString: " + cookieString + " urlString: " + urlString);
		return new DatabaseAuthorization(cookie);
	}
	public static DatabaseAuthorization create(Cookie cookie) {
		return new DatabaseAuthorization(cookie);
	}


	@JsonProperty("url")
	private String getUrlString() {
		return cookie.domain().toString();
	}

	@JsonProperty("cookie")
	private String getCookieString() {
		return cookie.toString();
	}
}

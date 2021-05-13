package me.retrodaredevil.couchdbjava.okhttp.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.okhttp.OkHttpCouchDbInstance;
import me.retrodaredevil.couchdbjava.okhttp.util.OkHttpUtil;
import me.retrodaredevil.couchdbjava.response.SessionPostResponse;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * An auth handler that sends a _session request, then uses the received cookie until it expires. This is designed
 * to only be used with one {@link OkHttpCouchDbInstance} at a time.
 */
public class CookieAuthHandler implements OkHttpAuthHandler {
	/*
	Also note while using this, "require_valid_user" should be false https://stackoverflow.com/questions/49443975/couchdb-cookie-authentication-is-not-working
	 */

	private static final ObjectMapper MAPPER = new ObjectMapper();
	private static final Logger LOGGER = LoggerFactory.getLogger(CookieAuthHandler.class);

	private final Object parallelLock = new Object();

	private Cookie authCookie;

	private String username;
	private String password;

	private volatile boolean autoAuth = true;

	public CookieAuthHandler(String username, String password) {
		updateCredentials(username, password);
	}
	public synchronized void updateCredentials(String username, String password) {
		requireNonNull(this.username = username);
		requireNonNull(this.password = password);
	}
	public void setAutoAuth(boolean autoAuth) {
		this.autoAuth = autoAuth;
	}
	private Cookie getAuthCookie() {
		Cookie r;
		synchronized (this) {
			r = authCookie;
		}
		if (r != null) {
			if (r.expiresAt() > System.currentTimeMillis()) {
				return r;
			}
		}
		return null;
	}
	public Long getExpiresAt() {
		Cookie cookie = getAuthCookie();
		if (cookie == null) {
			return null;
		}
		return cookie.expiresAt();
	}

	/**
	 * Manually authorizes the session. Note that internally, this handles being thread safe and prevents parallel calls to _session,
	 * but if you call this manually, you may call this in parallel if you don't manually handle that case. This is why it's best to
	 * let this automatically authenticate.
	 * @param instance The CouchDB instance object
	 * @return The response from the database
	 * @throws CouchDbException Thrown on connection errors, permission errors, etc
	 */
	public SessionPostResponse authSession(OkHttpCouchDbInstance instance) throws CouchDbException {
		final String username, password;
		synchronized (this) {
			username = this.username;
			password = this.password;
		}
		requireNonNull(username);
		requireNonNull(password);
		HttpUrl url = instance.createUrlBuilder().addPathSegment("_session").build();
		Map<String, String> map = new HashMap<>();
		map.put("name", username);
		map.put("password", password);
		String jsonData;
		try {
			jsonData = MAPPER.writeValueAsString(map);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		Request request = new Request.Builder()
				.url(url)
				.post(OkHttpUtil.createJsonRequestBody(jsonData))
				.build();
		Call call = instance.getClient().newCall(request);
		Response response = instance.executeCall(call);
		if (response.isSuccessful()) {
			List<Cookie> cookies = Cookie.parseAll(url, response.headers());
			Cookie authSessionCookie = null;
			for (Cookie c : cookies) {
				if ("AuthSession".equals(c.name())) {
					authSessionCookie = c;
					break;
				}
			}
			if (authSessionCookie == null) {
				throw new CouchDbException("No AuthSession cookie was set!");
			}
			LOGGER.debug("Got new authentication cookie that expires at " + authSessionCookie.expiresAt() + ". Persistent: " + authSessionCookie.persistent());
			synchronized (this) {
				this.authCookie = authSessionCookie;
			}
			SessionPostResponse sessionPostResponse;
			try {
				sessionPostResponse = MAPPER.readValue(requireNonNull(response.body()).byteStream(), SessionPostResponse.class);
			} catch (IOException e) {
				throw new CouchDbException("Received bad json data!", e);
			}
			return sessionPostResponse;
		}
		throw new CouchDbException("Bad response! code: " + response.code());
	}

	@Override
	public @Nullable CookieJar getCookieJar() {
		return cookieJar;
	}

	@Override
	public void preAuthorize(OkHttpCouchDbInstance instance) throws CouchDbException {
		if (autoAuth) {
			synchronized (parallelLock) {
				if (getAuthCookie() == null) {
					authSession(instance);
				}
			}
		}
	}

	@Override
	public void setAuthHeaders(OkHttpCouchDbInstance instance, Request.Builder requestBuilder) {
	}
	private final CookieJar cookieJar = new CookieJar() {
		@Override
		public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
		}

		@NotNull
		@Override
		public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
			Cookie authCookie = getAuthCookie();
			if (authCookie == null) {
				if (!httpUrl.encodedPathSegments().contains("_session")) {
					LOGGER.debug("authCookie was null, so not being used.");
				}
				return Collections.emptyList();
			}
			return Collections.singletonList(authCookie);
		}
	};
}

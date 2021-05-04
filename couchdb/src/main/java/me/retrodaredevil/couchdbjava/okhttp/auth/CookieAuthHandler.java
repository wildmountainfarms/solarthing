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

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class CookieAuthHandler implements OkHttpAuthHandler {
	private static final ObjectMapper MAPPER = new ObjectMapper();

	private Cookie authCookie;

	private String username;
	private String password;

	private boolean autoAuth = true;

	public CookieAuthHandler(String username, String password) {
		updateCredentials(username, password);
	}
	public void updateCredentials(String username, String password) {
		requireNonNull(this.username = username);
		requireNonNull(this.password = password);
	}
	public void setAutoAuth(boolean autoAuth) {
		this.autoAuth = autoAuth;
	}
	private Cookie getAuthCookie() {
		Cookie r = authCookie;
		if (r != null) {
			if (r.persistent() || authCookie.expiresAt() > System.currentTimeMillis()) {
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
	public SessionPostResponse authSession(OkHttpCouchDbInstance instance) throws CouchDbException {
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
			this.authCookie = authSessionCookie;
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
			if (getAuthCookie() == null) {
				authSession(instance);
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
				return Collections.emptyList();
			}
			return Collections.singletonList(authCookie);
		}
	};
}

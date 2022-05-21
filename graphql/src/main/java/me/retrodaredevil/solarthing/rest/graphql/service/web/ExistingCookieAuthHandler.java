package me.retrodaredevil.solarthing.rest.graphql.service.web;

import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.okhttp.OkHttpCouchDbInstance;
import me.retrodaredevil.couchdbjava.okhttp.auth.OkHttpAuthHandler;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Request;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class ExistingCookieAuthHandler implements OkHttpAuthHandler {
	private final Cookie cookie;

	private final CookieJar cookieJar = new CookieJar() {
		@Override
		public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {

		}

		@NotNull
		@Override
		public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
			return Collections.singletonList(cookie);
		}
	};

	public ExistingCookieAuthHandler(Cookie cookie) {
		this.cookie = cookie;
	}

	@Override
	public @Nullable CookieJar getCookieJar() {
		return cookieJar;
	}

	@Override
	public void preAuthorize(OkHttpCouchDbInstance instance) {
	}

	@Override
	public void setAuthHeaders(OkHttpCouchDbInstance instance, Request.Builder requestBuilder) {
	}
}

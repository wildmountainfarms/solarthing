package me.retrodaredevil.solarthing.rest.graphql.service.web;

import me.retrodaredevil.couchdbjava.okhttp.OkHttpCouchDbInstance;
import me.retrodaredevil.couchdbjava.okhttp.auth.OkHttpAuthHandler;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Request;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class ExistingCookieAuthHandler implements OkHttpAuthHandler {
	private final Cookie cookie;

	private final CookieJar cookieJar = new CookieJar() {
		@Override
		public void saveFromResponse(@NonNull HttpUrl httpUrl, @NonNull List<Cookie> list) {

		}

		@NonNull
		@Override
		public List<Cookie> loadForRequest(@NonNull HttpUrl httpUrl) {
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

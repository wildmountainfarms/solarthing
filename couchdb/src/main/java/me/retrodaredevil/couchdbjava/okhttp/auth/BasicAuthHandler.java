package me.retrodaredevil.couchdbjava.okhttp.auth;

import me.retrodaredevil.couchdbjava.CouchDbAuth;
import me.retrodaredevil.couchdbjava.okhttp.OkHttpCouchDbInstance;
import okhttp3.CookieJar;
import okhttp3.Request;
import org.jetbrains.annotations.Nullable;

public class BasicAuthHandler implements OkHttpAuthHandler {
	private final CouchDbAuth auth;

	public BasicAuthHandler(CouchDbAuth auth) {
		this.auth = auth;
	}

	@Override
	public @Nullable CookieJar getCookieJar() {
		return CookieJar.NO_COOKIES;
	}

	@Override
	public void preAuthorize(OkHttpCouchDbInstance instance) {

	}

	@Override
	public void setAuthHeaders(OkHttpCouchDbInstance instance, Request.Builder requestBuilder) {
		if (auth.usesAuth()) {
			requestBuilder.header("Authorization", "Basic " + auth.getBasicAuthString());
		}
	}
}

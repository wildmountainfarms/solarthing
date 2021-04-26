package me.retrodaredevil.couchdbjava.okhttp.auth;

import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.okhttp.OkHttpCouchDbInstance;
import okhttp3.CookieJar;
import okhttp3.Request;
import org.jetbrains.annotations.Nullable;

public interface OkHttpAuthHandler {
	@Nullable CookieJar getCookieJar();
	void preAuthorize(OkHttpCouchDbInstance instance) throws CouchDbException;
	void setAuthHeaders(OkHttpCouchDbInstance instance, Request.Builder requestBuilder);
}

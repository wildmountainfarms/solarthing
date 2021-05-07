package me.retrodaredevil.couchdbjava.okhttp.auth;

import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.okhttp.OkHttpCouchDbInstance;
import okhttp3.CookieJar;
import okhttp3.Request;
import org.jetbrains.annotations.Nullable;

public interface OkHttpAuthHandler {
	@Nullable CookieJar getCookieJar();

	/**
	 * If necessary, may make a call to the passed {@code instance} to authenticate with the database.
	 *
	 * If a necessary call is already being made to the database in another thread, it is expected that this will block until
	 * that thread is done, which should prevent unnecessary parallel calls.
	 *
	 * @param instance The CouchDB instance
	 * @throws CouchDbException Thrown on connection, permission, etc errors
	 */
	void preAuthorize(OkHttpCouchDbInstance instance) throws CouchDbException;

	void setAuthHeaders(OkHttpCouchDbInstance instance, Request.Builder requestBuilder);
}

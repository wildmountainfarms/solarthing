package me.retrodaredevil.couchdbjava.okhttp;

import me.retrodaredevil.couchdbjava.CouchDbDatabase;
import me.retrodaredevil.couchdbjava.CouchDbInstance;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.okhttp.auth.OkHttpAuthHandler;
import me.retrodaredevil.couchdbjava.okhttp.util.HeaderRequestInterceptor;
import me.retrodaredevil.couchdbjava.okhttp.util.OkHttpUtil;
import me.retrodaredevil.couchdbjava.response.CouchDbGetResponse;
import me.retrodaredevil.couchdbjava.response.SessionGetResponse;
import okhttp3.*;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class OkHttpCouchDbInstance implements CouchDbInstance {

	final OkHttpClient client;
	final HttpUrl url;
	final OkHttpAuthHandler authHandler;

	public OkHttpCouchDbInstance(OkHttpClient client, HttpUrl url, OkHttpAuthHandler authHandler) {
		OkHttpClient.Builder builder = client.newBuilder();
		CookieJar cookieJar = authHandler.getCookieJar();
		if (cookieJar != null) {
			builder.cookieJar(cookieJar);
		}
		this.client = builder
				.addInterceptor(new HeaderRequestInterceptor("Accept", "application/json"))
				.build();
		this.url = url;
		this.authHandler = authHandler;
	}
	public HttpUrl.Builder createUrlBuilder() {
		return new HttpUrl.Builder()
				.scheme(url.scheme())
				.host(url.host())
				.port(url.port())
				.query(url.query())
				;
	}
	public OkHttpClient getClient() {
		return client;
	}

	/**
	 * Simple helper method for executing a call to avoid try-cache boilerplate
	 */
	public Response executeCall(Call call) throws CouchDbException {
		try {
			return call.execute();
		} catch (IOException e) {
			throw new CouchDbException(e);
		}
	}

	@Override
	public CouchDbDatabase getDatabase(String path) {
		return null;
	}

	@Override
	public CouchDbDatabase getReplicatorDatabase() {
		return null;
	}

	@Override
	public CouchDbDatabase getUsersDatabase() {
		return null;
	}

	@Override
	public SessionGetResponse getSessionInfo() throws CouchDbException {
		preAuthorize();
		HttpUrl url = createUrlBuilder().addPathSegment("_session").build();
		Request request = new Request.Builder()
				.url(url)
				.get()
				.build();
		Call call = client.newCall(request);
		Response response = executeCall(call);
		if (response.isSuccessful()) {
			return OkHttpUtil.parseResponseBodyJson(response, SessionGetResponse.class);
		}
		throw new CouchDbException("Bad response! code: " + response.code());
	}

	@Override
	public CouchDbGetResponse getInfo() throws CouchDbException {
		preAuthorize();
		HttpUrl url = createUrlBuilder().build();
		Request request = new Request.Builder()
				.url(url)
				.get()
				.build();
		Call call = client.newCall(request);
		Response response = executeCall(call);
		if (response.isSuccessful()) {
			return OkHttpUtil.parseResponseBodyJson(response, CouchDbGetResponse.class);
		}
		throw new CouchDbException("Bad response! code: " + response.code());
	}

	void preAuthorize() throws CouchDbException {
		authHandler.preAuthorize(this);
	}
}

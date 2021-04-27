package me.retrodaredevil.couchdbjava.okhttp;

import me.retrodaredevil.couchdbjava.CouchDbDatabase;
import me.retrodaredevil.couchdbjava.CouchDbInstance;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.okhttp.auth.OkHttpAuthHandler;
import me.retrodaredevil.couchdbjava.okhttp.util.OkHttpUtil;
import me.retrodaredevil.couchdbjava.response.CouchDbGetResponse;
import me.retrodaredevil.couchdbjava.response.DatabaseInfo;
import me.retrodaredevil.couchdbjava.response.SessionGetResponse;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class OkHttpCouchDbInstance implements CouchDbInstance {

	private final OkHttpClient client;
	private final HttpUrl url;
	private final OkHttpAuthHandler authHandler;

	private final CouchDbService service;

	private final OkHttpCouchDbDatabase replicatorDatabase;
	private final OkHttpCouchDbDatabase usersDatabase;

	public OkHttpCouchDbInstance(OkHttpClient client, HttpUrl url, OkHttpAuthHandler authHandler) {
		requireNonNull(this.url = url);
		requireNonNull(this.authHandler = authHandler);

		OkHttpClient.Builder builder = client.newBuilder();
		CookieJar cookieJar = authHandler.getCookieJar();
		if (cookieJar != null) {
			builder.cookieJar(cookieJar);
		}
		this.client = builder
//				.addInterceptor(new HeaderRequestInterceptor("Accept", "application/json"))
//				.addInterceptor(new HttpLoggingInterceptor(System.out::println).setLevel(HttpLoggingInterceptor.Level.BODY))
				.build();

		Retrofit retrofit = new Retrofit.Builder()
				.client(this.client)
				.baseUrl(createUrlBuilder().build())
				.addConverterFactory(JacksonConverterFactory.create())
				.addConverterFactory(ScalarsConverterFactory.create())
				.build()
				;
		service = retrofit.create(CouchDbService.class);

		replicatorDatabase = getDatabase("_replicator");
		usersDatabase = getDatabase("_users");
	}
	public HttpUrl.Builder createUrlBuilderNoQuery() {
		return new HttpUrl.Builder()
				.scheme(url.scheme())
				.host(url.host())
				.port(url.port())
				;
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
	public <T> T executeAndHandle(retrofit2.Call<T> call) throws CouchDbException {
		retrofit2.Response<T> response = executeCall(call);
		if (response.isSuccessful()) {
			return response.body();
		}

		throw OkHttpUtil.createExceptionFromResponse(response);
	}
	public <T> retrofit2.Response<T> executeCall(retrofit2.Call<T> call) throws CouchDbException {
		try {
			return call.execute();
		} catch (IOException e) {
			throw new CouchDbException("Connection failed!", e);
		}
	}

	@Override
	public OkHttpCouchDbDatabase getDatabase(String path) {
		return new OkHttpCouchDbDatabase(path, this);
	}

	@Override
	public CouchDbDatabase getReplicatorDatabase() {
		return replicatorDatabase;
	}

	@Override
	public CouchDbDatabase getUsersDatabase() {
		return usersDatabase;
	}

	@Override
	public SessionGetResponse getSessionInfo() throws CouchDbException {
		preAuthorize();
		return executeAndHandle(service.getSessionInfo());
	}

	@Override
	public CouchDbGetResponse getInfo() throws CouchDbException {
		preAuthorize();
		return executeAndHandle(service.getInfo());
	}

	@Override
	public List<String> getAllDatabaseNames() throws CouchDbException {
		preAuthorize();
		return executeAndHandle(service.getAllDatabaseNames());
	}

	@Override
	public List<DatabaseInfo> getDatabaseInfos(List<String> databaseNames) throws CouchDbException {
		preAuthorize();
		Map<String, List<String>> map = new HashMap<>();
		map.put("keys", databaseNames);
		return executeAndHandle(service.getDatabaseInfos(map));
	}

	void preAuthorize() throws CouchDbException {
		authHandler.preAuthorize(this);
	}
}

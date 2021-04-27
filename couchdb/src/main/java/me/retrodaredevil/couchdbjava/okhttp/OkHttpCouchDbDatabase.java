package me.retrodaredevil.couchdbjava.okhttp;

import me.retrodaredevil.couchdbjava.CouchDbDatabase;
import me.retrodaredevil.couchdbjava.CouchDbStatusCode;
import me.retrodaredevil.couchdbjava.exception.CouchDbCodeException;
import me.retrodaredevil.couchdbjava.okhttp.util.OkHttpUtil;
import me.retrodaredevil.couchdbjava.response.DatabaseInfo;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.exception.CouchDbUnauthorizedException;
import me.retrodaredevil.couchdbjava.response.ErrorResponse;
import okhttp3.*;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.io.IOException;

public class OkHttpCouchDbDatabase implements CouchDbDatabase {
	private static final String DATABASE_REGEX = "^[a-z][a-z0-9_$()+/-]*$";
	private final String name;
	private final OkHttpCouchDbInstance instance;
	private final CouchDbDatabaseService service;

	public OkHttpCouchDbDatabase(String name, OkHttpCouchDbInstance instance) {
		if (name.startsWith("_") ? !name.substring(1).matches(DATABASE_REGEX) : !name.matches(DATABASE_REGEX)) {
			throw new IllegalArgumentException("Invalid database name! name: " + name);
		}
		this.name = name;
		this.instance = instance;

		Retrofit retrofit = new Retrofit.Builder()
				.client(instance.getClient())
				.baseUrl(instance.createUrlBuilderNoQuery().addPathSegment(name).addEncodedPathSegments("/").build())
				.addConverterFactory(JacksonConverterFactory.create())
				.addConverterFactory(ScalarsConverterFactory.create())
				.build()
				;
		service = retrofit.create(CouchDbDatabaseService.class);
	}
	private HttpUrl.Builder createUrlBuilder() {
		return instance.createUrlBuilder().addPathSegment(name);
	}

	@Override
	public boolean exists() throws CouchDbException {
		instance.preAuthorize();
		retrofit2.Response<Void> response = instance.executeCall(service.checkExists());
		if (response.isSuccessful()) {
			return true;
		}
		if (response.code() == CouchDbStatusCode.NOT_FOUND) {
			return false;
		}
		throw OkHttpUtil.createExceptionFromResponse(response.raw());
	}

	@Override
	public boolean createIfNotExists() throws CouchDbException {
		try {
			create();
			return true;
		} catch (CouchDbCodeException exception) {
			if (exception.getCode() == CouchDbStatusCode.PRECONDITION_FAILED) {
				return false;
			}
			throw exception;
		}
	}

	@Override
	public void create() throws CouchDbException {
		instance.preAuthorize();
		instance.executeAndHandle(service.createDatabase());
	}

	@Override
	public void delete() throws CouchDbException {
		instance.preAuthorize();
		instance.executeAndHandle(service.deleteDatabase());
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public DatabaseInfo getDatabaseInfo() throws CouchDbException {
		instance.preAuthorize();
		return instance.executeAndHandle(service.getInfo());
	}

	@Override
	public String createWithoutIdRaw(String json) {
		return null;
	}

	@Override
	public String putRaw(String id, String json) {
		return null;
	}

	@Override
	public String updateWithRevision(String id, String revision, String json) {
		return null;
	}

	@Override
	public void delete(String id, String revision) {

	}

	@Override
	public String findRaw(String id) {
		return null;
	}

	@Override
	public String getCurrentRevision(String id) {
		return null;
	}

	@Override
	public String copy(String sourceId, String targetId, String targetRevision) {
		return null;
	}
}

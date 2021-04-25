package me.retrodaredevil.couchdbjava.okhttp;

import me.retrodaredevil.couchdbjava.CouchDbDatabase;
import me.retrodaredevil.couchdbjava.CouchDbStatusCode;
import me.retrodaredevil.couchdbjava.DatabaseInfo;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.exception.CouchDbUnauthorizedException;
import okhttp3.*;

public class OkHttpCouchDbDatabase implements CouchDbDatabase {
	private final String name;
	private final OkHttpCouchDbInstance instance;

	public OkHttpCouchDbDatabase(String name, OkHttpCouchDbInstance instance) {
		this.name = name;
		this.instance = instance;
	}
	private HttpUrl.Builder createUrlBuilder() {
		return instance.createUrlBuilder().addPathSegment(name);
	}

	@Override
	public boolean exists() throws CouchDbException {
		Call call = instance.client.newCall(
				new Request.Builder()
						.head()
						.url(createUrlBuilder().build())
						.build()
		);
		Response response = instance.executeCall(call);
		if (response.isSuccessful()) {
			return true;
		}
		if (response.code() == 404) {
			return false;
		}
		throw new CouchDbException("Unknown response code: " + response.code());
	}

	@Override
	public boolean createIfNotExists() throws CouchDbException {
		Call call = instance.client.newCall(
				new Request.Builder()
						.put(FormBody.create(new byte[0]))
						.url(createUrlBuilder().build())
						.build()
		);
		Response response = instance.executeCall(call);
		if (response.isSuccessful()) {
			return true;
		}
		switch (response.code()) {
			case CouchDbStatusCode.PRECONDITION_FAILED:
				return false;
			case CouchDbStatusCode.UNAUTHORIZED:
				throw new CouchDbUnauthorizedException("You are unauthorized!");
			case CouchDbStatusCode.BAD_REQUEST:
				throw new CouchDbException("Bad request! (Bad database name?) database name: " + name);
		}
		throw new CouchDbException("Unexpected response code while attempting to create database! code: " + response.code());
	}

	@Override
	public void delete() {

	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public DatabaseInfo getDatabaseInfo() {
		return null;
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

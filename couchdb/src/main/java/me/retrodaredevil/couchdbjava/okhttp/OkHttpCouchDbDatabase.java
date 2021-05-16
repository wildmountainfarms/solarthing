package me.retrodaredevil.couchdbjava.okhttp;

import me.retrodaredevil.couchdbjava.CouchDbDatabase;
import me.retrodaredevil.couchdbjava.CouchDbStatusCode;
import me.retrodaredevil.couchdbjava.request.BulkGetRequest;
import me.retrodaredevil.couchdbjava.request.ViewQueryParams;
import me.retrodaredevil.couchdbjava.exception.CouchDbCodeException;
import me.retrodaredevil.couchdbjava.json.JsonData;
import me.retrodaredevil.couchdbjava.json.StringJsonData;
import me.retrodaredevil.couchdbjava.okhttp.util.OkHttpUtil;
import me.retrodaredevil.couchdbjava.option.DatabaseCreationOption;
import me.retrodaredevil.couchdbjava.response.*;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.security.DatabaseSecurity;
import okhttp3.*;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

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
				.baseUrl(instance.createUrlBuilderNoQuery().addPathSegment(name).addEncodedPathSegments("").build())
				.addConverterFactory(JacksonConverterFactory.create())
				.addConverterFactory(ScalarsConverterFactory.create())
				.build()
				;
//		System.out.println(retrofit.baseUrl());
		service = retrofit.create(CouchDbDatabaseService.class);
	}
	private static String encodeDocumentId(String documentId) {
		if (documentId.startsWith("_design/")) {
			return "_design/" + encodeDocumentId(documentId.substring("_design/".length()));
		}
		try {
			return URLEncoder.encode(documentId, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("Could not encode: '" + documentId + "'.", e);
		}
	}
	private static String encodeRevisionForHeader(String revision) {
		requireNonNull(revision);
		return '"' + revision + '"';
	}
	private HttpUrl.Builder createUrlBuilder() {
		return instance.createUrlBuilder().addPathSegment(name);
	}

	@Override
	public String getName() {
		return name;
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
		throw OkHttpUtil.createExceptionFromResponse(response);
	}

	@Override
	public void create(DatabaseCreationOption databaseCreationOption) throws CouchDbException {
		instance.preAuthorize();
		Map<String, Object> map = new HashMap<>();
		if (databaseCreationOption.getShards() != null) {
			map.put("q", databaseCreationOption.getShards());
		}
		if (databaseCreationOption.getReplicas() != null) {
			map.put("n", databaseCreationOption.getReplicas());
		}
		if (databaseCreationOption.getPartitioned() != null) {
			map.put("partitioned", databaseCreationOption.getPartitioned());
		}
		instance.executeAndHandle(service.createDatabase(map));
	}

	@Override
	public boolean createIfNotExists(DatabaseCreationOption databaseCreationOption) throws CouchDbException {
		try {
			create(databaseCreationOption);
			return true;
		} catch (CouchDbCodeException exception) {
			if (exception.getCode() == CouchDbStatusCode.PRECONDITION_FAILED) {
				return false;
			}
			throw exception;
		}
	}

	@Override
	public void deleteDatabase() throws CouchDbException {
		instance.preAuthorize();
		instance.executeAndHandle(service.deleteDatabase());
	}

	@Override
	public DatabaseInfo getDatabaseInfo() throws CouchDbException {
		instance.preAuthorize();
		return instance.executeAndHandle(service.getInfo());
	}

	@Override
	public DocumentResponse postNewDocument(JsonData jsonData) throws CouchDbException {
		instance.preAuthorize();
		return instance.executeAndHandle(service.postDocument(OkHttpUtil.createJsonRequestBody(jsonData)));
	}

	@Override
	public DocumentResponse putDocument(String id, JsonData jsonData) throws CouchDbException {
		instance.preAuthorize();
		return instance.executeAndHandle(service.putDocument(encodeDocumentId(id), null, OkHttpUtil.createJsonRequestBody(jsonData)));
	}


	@Override
	public DocumentResponse updateDocument(String id, String revision, JsonData jsonData) throws CouchDbException {
		instance.preAuthorize();
		return instance.executeAndHandle(service.putDocument(requireNonNull(encodeDocumentId(id)), encodeRevisionForHeader(revision), OkHttpUtil.createJsonRequestBody(jsonData)));
	}

	@Override
	public DocumentResponse deleteDocument(String id, String revision) throws CouchDbException {
		instance.preAuthorize();
		return instance.executeAndHandle(service.deleteDocument(requireNonNull(encodeDocumentId(id)), requireNonNull(revision)));
	}

	@Override
	public DocumentData getDocument(String id) throws CouchDbException {
		return getDocumentIfUpdated(id, null);
	}
	@Override
	public DocumentData getDocumentIfUpdated(String id, String revision) throws CouchDbException {
		instance.preAuthorize();
		Request.Builder builder = new Request.Builder()
				.get()
				.url(createUrlBuilder().addEncodedPathSegments(encodeDocumentId(id)).build());
		if (revision != null) {
			builder.header("If-None-Match", revision);
		}
		Response response = instance.executeCall(instance.getClient().newCall(builder.build()));
		if (response.isSuccessful()) {
			String json;
			try {
				json = requireNonNull(response.body()).string();
			} catch (IOException e) {
				throw new CouchDbException("Couldn't read response!", e);
			}
			return new DocumentData(getRevision(response), new StringJsonData(json));
		}
		throw OkHttpUtil.createExceptionFromResponse(response);

	}

	@Override
	public String getCurrentRevision(String id) throws CouchDbException {
		instance.preAuthorize();
		Response response = instance.executeCall(instance.getClient().newCall(
				new Request.Builder()
						.head()
						.url(createUrlBuilder().addEncodedPathSegments(encodeDocumentId(id)).build())
						.build()
		));
		if (response.isSuccessful()) {
			return getRevision(response);
		}
		throw OkHttpUtil.createExceptionFromResponse(response);
	}
	private String getRevision(Response response) throws CouchDbException {
		String revision = response.header("ETag");
		if (revision == null) {
			throw new CouchDbException("ETag header was not present!");
		}
		if (revision.length() < 36) { // minimum length of a revision is 34, plus two for the two double quotes
			throw new CouchDbException("Revision length is too small! revision: " + revision);
		}
		return revision.substring(1, revision.length() - 1); // trim off the double quotes
	}

	@Override
	public DocumentResponse copyToNewDocument(String id, String newDocumentId) throws CouchDbException {
		instance.preAuthorize();
		return instance.executeAndHandle(service.copyToDocument(encodeDocumentId(id), newDocumentId));
	}

	@Override
	public DocumentResponse copyFromRevisionToNewDocument(String id, String revision, String newDocumentId) throws CouchDbException {
		instance.preAuthorize();
		return instance.executeAndHandle(service.copyFromRevisionToDocument(encodeDocumentId(id), revision, newDocumentId));
	}

	@Override
	public DocumentResponse copyToExistingDocument(String id, String targetDocumentId, String targetDocumentRevision) throws CouchDbException {
		instance.preAuthorize();
		return instance.executeAndHandle(service.copyToDocument(encodeDocumentId(id), targetDocumentId + "?rev=" + targetDocumentRevision));
	}

	@Override
	public DocumentResponse copyFromRevisionToExistingDocument(String id, String revision, String targetDocumentId, String targetDocumentRevision) throws CouchDbException {
		instance.preAuthorize();
		return instance.executeAndHandle(service.copyFromRevisionToDocument(encodeDocumentId(id), revision, targetDocumentId + "?rev=" + targetDocumentRevision));
	}

	@Override
	public ViewResponse queryView(String designDoc, String viewName, ViewQueryParams viewQueryParams) throws CouchDbException {
		designDoc = designDoc.replaceAll("_design/", ""); // Just in case the user added _design/ to this, let's make that valid
		instance.preAuthorize();
		return instance.executeAndHandle(service.queryView(encodeDocumentId(designDoc), viewName, viewQueryParams));
	}

	@Override
	public DatabaseSecurity getSecurity() throws CouchDbException {
		instance.preAuthorize();
		return instance.executeAndHandle(service.getSecurity());
	}

	@Override
	public void setSecurity(DatabaseSecurity databaseSecurity) throws CouchDbException {
		instance.preAuthorize();
		instance.executeAndHandle(service.putSecurity(databaseSecurity));
	}

	@Override
	public BulkGetResponse getDocumentsBulk(BulkGetRequest request) throws CouchDbException {
		instance.preAuthorize();
		return instance.executeAndHandle(service.getDocumentsBulk(request));
	}
}

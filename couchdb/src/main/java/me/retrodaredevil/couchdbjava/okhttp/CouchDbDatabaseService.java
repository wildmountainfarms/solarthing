package me.retrodaredevil.couchdbjava.okhttp;

import me.retrodaredevil.couchdbjava.response.DatabaseInfo;
import me.retrodaredevil.couchdbjava.response.DocumentResponse;
import me.retrodaredevil.couchdbjava.response.SimpleStatus;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.Map;

public interface CouchDbDatabaseService {

	@HEAD("/")
	Call<Void> checkExists();

	/**
	 * https://docs.couchdb.org/en/stable/api/database/common.html#put--db
	 */
	@PUT("/")
	Call<SimpleStatus> createDatabase();

	@GET("/")
	Call<DatabaseInfo> getInfo();

	@DELETE("/")
	Call<SimpleStatus> deleteDatabase();

	/**
	 * Puts the document into the database. If the JSON data contains a `_id` field, that will be used
	 * for the ID. Otherwise, a random ID will be generated.
	 *
	 * This does not support updating an existing document
	 */
	@POST("/")
	Call<DocumentResponse> postDocument(@Body RequestBody jsonRequestBody, @QueryMap Map<String, String> queryMap);

	/**
	 * Puts the document into the database with the given id.
	 */
	@PUT("/{docid}")
	Call<DocumentResponse> putDocument(@Path("docid") String docid, @Body RequestBody jsonRequestBody);
}

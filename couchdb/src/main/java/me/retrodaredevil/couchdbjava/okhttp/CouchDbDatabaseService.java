package me.retrodaredevil.couchdbjava.okhttp;

import me.retrodaredevil.couchdbjava.response.DatabaseInfo;
import me.retrodaredevil.couchdbjava.response.DocumentResponse;
import me.retrodaredevil.couchdbjava.response.SimpleStatus;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.Map;

public interface CouchDbDatabaseService {

	@HEAD("./")
	Call<Void> checkExists();

	/**
	 * https://docs.couchdb.org/en/stable/api/database/common.html#put--db
	 */
	@PUT("./")
	Call<SimpleStatus> createDatabase(@QueryMap Map<String, Object> queryMap);

	@GET("./")
	Call<DatabaseInfo> getInfo();

	@DELETE("./")
	Call<SimpleStatus> deleteDatabase();

	/**
	 * Puts the document into the database. If the JSON data contains a `_id` field, that will be used
	 * for the ID. Otherwise, a random ID will be generated.
	 *
	 * This does not support updating an existing document
	 */
	@POST("./")
	Call<DocumentResponse> postDocument(@Body RequestBody jsonRequestBody);

	/**
	 * Puts the document into the database with the given id.
	 * @param revision The revision of the existing document or null if this is a new document. This could also be null if you put the revision in the body.
	 */
	@PUT("./{docid}")
	Call<DocumentResponse> putDocument(@Path("docid") String docid, @Header("Match-If") String revision, @Body RequestBody jsonRequestBody);

	@DELETE("./{docid}")
	Call<DocumentResponse> deleteDocument(@Path("docid") String docid, @Header("Match-If") String revision);


	@HTTP(method = "COPY", path = "./{docid}")
	Call<DocumentResponse> copyToDocument(@Path("docid") String docid, @Header("Destination") String newDocumentId);

	@HTTP(method = "COPY", path = "./{docid}")
	Call<DocumentResponse> copyFromRevisionToDocument(@Path("docid") String docid, @Query("rev") String revision, @Header("Destination") String newDocumentId);
}

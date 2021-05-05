package me.retrodaredevil.couchdbjava;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import me.retrodaredevil.couchdbjava.exception.CouchDbCodeException;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.okhttp.OkHttpCouchDbInstance;
import me.retrodaredevil.couchdbjava.okhttp.auth.CookieAuthHandler;
import me.retrodaredevil.couchdbjava.response.CouchDbGetResponse;
import me.retrodaredevil.couchdbjava.response.DocumentResponse;
import me.retrodaredevil.couchdbjava.response.SessionGetResponse;
import me.retrodaredevil.couchdbjava.response.SessionPostResponse;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class CouchDbTestMain {
	private static final ObjectMapper MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

	public static void main(String[] args) throws CouchDbException, JsonProcessingException {
		try {
			doMain();
		} catch (CouchDbCodeException ex) {
			System.out.println(MAPPER.writeValueAsString(ex.getErrorResponse()));
			throw ex;
		}
	}
	private static void doMain() throws CouchDbException, JsonProcessingException {
		CookieAuthHandler authHandler = new CookieAuthHandler("admin", "relax");
		authHandler.setAutoAuth(false);

		OkHttpCouchDbInstance instance = new OkHttpCouchDbInstance(
				new OkHttpClient.Builder()
						.addInterceptor(new HttpLoggingInterceptor(System.out::println).setLevel(HttpLoggingInterceptor.Level.BODY))
						.build(),
				new HttpUrl.Builder()
						.scheme("http")
						.host("localhost")
						.port(5984)
						.build(),
				authHandler
		);
		SessionPostResponse postResponse = authHandler.authSession(instance);
		System.out.println(MAPPER.writeValueAsString(postResponse));

		SessionGetResponse getResponse = instance.getSessionInfo();
		System.out.println(MAPPER.writeValueAsString(getResponse));

		CouchDbGetResponse statusResponse = instance.getInfo();
		System.out.println(MAPPER.writeValueAsString(statusResponse));

		System.out.println(MAPPER.writeValueAsString(instance.getAllDatabaseNames()));

		CouchDbDatabase database = instance.getDatabase("test");
		database.createIfNotExists();
		DocumentResponse documentResponse = database.postNewDocument("{\"hi\": 3}");
		System.out.println(MAPPER.writeValueAsString(documentResponse));
		System.out.println(database.getCurrentRevision(documentResponse.getId()));
		System.out.println(database.getDocument(documentResponse.getId()).getJsonData().getJson());

		System.out.println(database.queryView(
				"packets",
				"millis",
				new ViewQueryBuilder()
						.startKey(System.currentTimeMillis() - 5 * 60 * 1000)
						.build()
		));
	}
}

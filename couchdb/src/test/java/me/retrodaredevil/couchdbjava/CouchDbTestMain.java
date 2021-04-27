package me.retrodaredevil.couchdbjava;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.okhttp.OkHttpCouchDbInstance;
import me.retrodaredevil.couchdbjava.okhttp.auth.CookieAuthHandler;
import me.retrodaredevil.couchdbjava.response.CouchDbGetResponse;
import me.retrodaredevil.couchdbjava.response.SessionGetResponse;
import me.retrodaredevil.couchdbjava.response.SessionPostResponse;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public class CouchDbTestMain {
	private static final ObjectMapper MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

	public static void main(String[] args) throws CouchDbException, JsonProcessingException {
		CookieAuthHandler authHandler = new CookieAuthHandler("admin", "relax");
		authHandler.setAutoAuth(false);

		OkHttpCouchDbInstance instance = new OkHttpCouchDbInstance(
				new OkHttpClient.Builder()
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
	}
}

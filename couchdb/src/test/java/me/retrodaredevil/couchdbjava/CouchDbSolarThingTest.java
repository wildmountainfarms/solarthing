package me.retrodaredevil.couchdbjava;

import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.okhttp.OkHttpCouchDbInstance;
import me.retrodaredevil.couchdbjava.okhttp.auth.BasicAuthHandler;
import me.retrodaredevil.couchdbjava.response.ViewResponse;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class CouchDbSolarThingTest {
	public static void main(String[] args) throws CouchDbException {
		OkHttpCouchDbInstance instance = new OkHttpCouchDbInstance(
				new OkHttpClient.Builder()
						.addInterceptor(new HttpLoggingInterceptor(System.out::println).setLevel(HttpLoggingInterceptor.Level.BODY))
						.build(),
				new HttpUrl.Builder()
						.scheme("https")
						.host("<redacted>")
						.port(443)
						.build(),
				new BasicAuthHandler(CouchDbAuth.createNoAuth())
		);
		ViewResponse response = instance.getDatabase("solarthing").queryView(
				"packets",
				"millis",
				new ViewQueryBuilder()
						.startKey(9999999999999L)
						.limit(10)
						.descending()
						.build()
		);
	}
}

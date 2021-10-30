package me.retrodaredevil.couchdb;


import me.retrodaredevil.couchdbjava.CouchDbAuth;
import me.retrodaredevil.couchdbjava.CouchDbInstance;
import me.retrodaredevil.couchdbjava.okhttp.OkHttpCouchDbInstance;
import me.retrodaredevil.couchdbjava.okhttp.auth.BasicAuthHandler;
import me.retrodaredevil.couchdbjava.okhttp.auth.CookieAuthHandler;
import me.retrodaredevil.couchdbjava.okhttp.auth.OkHttpAuthHandler;
import me.retrodaredevil.okhttp3.OkHttpProperties;
import me.retrodaredevil.okhttp3.OkHttpUtil;
import me.retrodaredevil.solarthing.annotations.UtilityClass;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

@UtilityClass
public final class CouchDbUtil {
	private CouchDbUtil() { throw new UnsupportedOperationException(); }
	public static CouchDbInstance createInstance(CouchProperties couchProperties, OkHttpProperties okHttpProperties) {
		return createInstance(couchProperties, okHttpProperties, false);
	}

	public static CouchDbInstance createInstance(CouchProperties couchProperties, OkHttpProperties okHttpProperties, boolean debug) {
		OkHttpClient.Builder builder = OkHttpUtil.createBuilder(okHttpProperties);
		if (debug) {
			builder.addInterceptor(new HttpLoggingInterceptor(System.out::println).setLevel(HttpLoggingInterceptor.Level.BODY));
		}
		return createInstance(
				couchProperties,
				builder
						.build()
		);
	}
	public static CouchDbInstance createInstance(CouchProperties couchProperties, OkHttpClient okHttpClient) {

		final OkHttpAuthHandler authHandler;
		String username = couchProperties.getUsername();
		String password = couchProperties.getPassword();
		if (username != null) {
			if (password == null) {
				throw new IllegalArgumentException("If username isn't null, then password cannot be null!");
			}
			if (couchProperties.useBasicAuth()) {
				authHandler = new BasicAuthHandler(CouchDbAuth.create(username, password));
			} else {
				authHandler = new CookieAuthHandler(username, password);
			}
		} else {
			if (password != null) {
				throw new IllegalArgumentException("If username is null, then you shouldn't have a password set!");
			}
			authHandler = new BasicAuthHandler(CouchDbAuth.createNoAuth());
		}

		return new OkHttpCouchDbInstance(
				okHttpClient,
				couchProperties.getHttpUrl(),
				authHandler
		);
	}

}

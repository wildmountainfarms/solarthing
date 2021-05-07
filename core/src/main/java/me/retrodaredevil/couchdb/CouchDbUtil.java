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

@UtilityClass
public final class CouchDbUtil {
	private CouchDbUtil() { throw new UnsupportedOperationException(); }

	public static CouchDbInstance createInstance(CouchProperties couchProperties, OkHttpProperties okHttpProperties) {
		OkHttpClient.Builder builder = OkHttpUtil.createBuilder(okHttpProperties);

		final OkHttpAuthHandler authHandler;
		String username = couchProperties.getUsername();
		String password = couchProperties.getPassword();
		if (username != null) {
			if (password == null) {
				throw new IllegalArgumentException("If username isn't null, then password cannot be null!");
			}
			authHandler = new CookieAuthHandler(username, password);
		} else {
			authHandler = new BasicAuthHandler(CouchDbAuth.createNoAuth());
		}

		return new OkHttpCouchDbInstance(
				builder
//						.addInterceptor(new HttpLoggingInterceptor(System.out::println).setLevel(HttpLoggingInterceptor.Level.BODY))
						.build(),
				couchProperties.getHttpUrl(),
				authHandler
		);
	}

}

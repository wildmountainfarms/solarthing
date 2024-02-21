package me.retrodaredevil.solarthing.integration;

import me.retrodaredevil.couchdbjava.CouchDbAuth;
import me.retrodaredevil.couchdbjava.CouchDbInstance;
import me.retrodaredevil.couchdbjava.okhttp.OkHttpCouchDbInstance;
import me.retrodaredevil.couchdbjava.okhttp.auth.BasicAuthHandler;
import me.retrodaredevil.solarthing.SolarThingDatabaseType;
import me.retrodaredevil.solarthing.annotations.UtilityClass;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

@UtilityClass
public final class IntegrationUtil {
	private IntegrationUtil() { throw new UnsupportedOperationException(); }

	public static final CouchDbAuth DEFAULT_ADMIN_AUTH = CouchDbAuth.create("admin", "password");

	public static CouchDbInstance createCouchDbInstance(DatabaseService databaseService, CouchDbAuth auth) {
		return createCouchDbInstance(databaseService, auth, false);
	}
	public static CouchDbInstance createDebugCouchDbInstance(DatabaseService databaseService, CouchDbAuth auth) {
		return createCouchDbInstance(databaseService, auth, true);
	}

	private static CouchDbInstance createCouchDbInstance(DatabaseService databaseService, CouchDbAuth auth, boolean debug) {
		OkHttpClient.Builder builder = new OkHttpClient.Builder();
		if (debug){
			builder.addInterceptor(new HttpLoggingInterceptor(System.out::println).setLevel(HttpLoggingInterceptor.Level.BODY));
		}
		return new OkHttpCouchDbInstance(
				builder.build(),
				new HttpUrl.Builder()
						.scheme("http")
						.host(databaseService.getHost())
						.port(databaseService.getPort())
						.build(),
				new BasicAuthHandler(auth)
		);
	}
	public static CouchDbAuth getAuthFor(SolarThingDatabaseType.UserType userType) {
		return CouchDbAuth.create(userType.getRecommendedName(), userType.getRecommendedName() + "password!123");
	}
}

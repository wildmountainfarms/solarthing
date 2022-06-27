package me.retrodaredevil.okhttp3;

import me.retrodaredevil.solarthing.annotations.UtilityClass;
import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

@UtilityClass
public final class OkHttpUtil {
	private OkHttpUtil() { throw new UnsupportedOperationException(); }

	@SuppressWarnings("PreferJavaTimeOverload") // TODO consider refactoring OkHttpProperties to use Java 8 time stuff
	public static OkHttpClient.Builder createBuilder(OkHttpProperties okHttpProperties) {
		return new OkHttpClient.Builder()
				.retryOnConnectionFailure(okHttpProperties.isRetryOnConnectionFailure())
				.callTimeout(okHttpProperties.getCallTimeoutMillis(), TimeUnit.MILLISECONDS)
				.connectTimeout(okHttpProperties.getConnectTimeoutMillis(), TimeUnit.MILLISECONDS)
				.readTimeout(okHttpProperties.getReadTimeoutMillis(), TimeUnit.MILLISECONDS)
				.writeTimeout(okHttpProperties.getWriteTimeoutMillis(), TimeUnit.MILLISECONDS)
				.pingInterval(okHttpProperties.getPingIntervalMillis(), TimeUnit.MILLISECONDS);
	}
}

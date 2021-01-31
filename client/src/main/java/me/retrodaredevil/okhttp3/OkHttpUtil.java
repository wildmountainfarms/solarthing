package me.retrodaredevil.okhttp3;

import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

public final class OkHttpUtil {
	private OkHttpUtil() { throw new UnsupportedOperationException(); }

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

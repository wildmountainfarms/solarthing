package me.retrodaredevil.solarthing.serviceutil;

import okhttp3.Interceptor;
import okhttp3.Response;
import org.jspecify.annotations.NonNull;

import java.io.IOException;

public class HeaderRequestInterceptor implements Interceptor {
	private final String name;
	private final String value;

	public HeaderRequestInterceptor(String name, String value) {
		this.name = name;
		this.value = value;
	}

	@NonNull
	@Override
	public Response intercept(@NonNull Chain chain) throws IOException {
		return chain.proceed(chain.request().newBuilder()
				.header(name, value)
				.build()
		);
	}
}

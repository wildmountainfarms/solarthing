package me.retrodaredevil.couchdbjava.okhttp.util;

import okhttp3.Interceptor;
import okhttp3.Response;

import javax.annotation.Nonnull;
import java.io.IOException;

public class HeaderRequestInterceptor implements Interceptor {
	private final String name;
	private final String value;

	public HeaderRequestInterceptor(String name, String value) {
		this.name = name;
		this.value = value;
	}

	@Override
	public @Nonnull Response intercept(@Nonnull Chain chain) throws IOException {
		return chain.proceed(chain.request().newBuilder()
				.header(name, value)
				.build()
		);
	}
}

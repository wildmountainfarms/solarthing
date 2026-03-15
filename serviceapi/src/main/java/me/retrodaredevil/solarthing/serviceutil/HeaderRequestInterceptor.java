package me.retrodaredevil.solarthing.serviceutil;

import okhttp3.Interceptor;
import okhttp3.Response;
import org.jspecify.annotations.NullMarked;

import java.io.IOException;

@NullMarked
public class HeaderRequestInterceptor implements Interceptor {
	private final String name;
	private final String value;

	public HeaderRequestInterceptor(String name, String value) {
		this.name = name;
		this.value = value;
	}

	@Override
	public Response intercept(Chain chain) throws IOException {
		return chain.proceed(chain.request().newBuilder()
				.header(name, value)
				.build()
		);
	}
}

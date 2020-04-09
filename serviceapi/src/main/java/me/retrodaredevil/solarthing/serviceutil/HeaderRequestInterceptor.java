package me.retrodaredevil.solarthing.serviceutil;

import okhttp3.Interceptor;
import okhttp3.Response;
import javax.validation.constraints.NotNull;

import java.io.IOException;

public class HeaderRequestInterceptor implements Interceptor {
	private final String name;
	private final String value;

	public HeaderRequestInterceptor(String name, String value) {
		this.name = name;
		this.value = value;
	}

	@NotNull
	@Override
	public Response intercept(@NotNull Chain chain) throws IOException {
		return chain.proceed(chain.request().newBuilder()
				.header(name, value)
				.build()
		);
	}
}

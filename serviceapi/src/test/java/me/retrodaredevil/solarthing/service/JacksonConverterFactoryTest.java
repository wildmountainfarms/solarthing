package me.retrodaredevil.solarthing.service;

import me.retrodaredevil.solarthing.util.JacksonUtil;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;
import retrofit2.Converter;
import retrofit2.Retrofit;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.*;

@NullMarked
class JacksonConverterFactoryTest {
	private static final Retrofit RETROFIT = new Retrofit.Builder()
			.baseUrl("http://localhost/")
			.addConverterFactory(JacksonConverterFactory.create(JacksonUtil.defaultMapper()))
			.build();
	private static final Annotation[] NO_ANNOTATIONS = new Annotation[0];

	public record Payload(String name, int count) {}

	@Test
	void convertsGenericRequestAndResponse() throws IOException {
		var type = new TypeReference<List<Payload>>() {}.getType();
		Converter<List<Payload>, RequestBody> requestConverter = RETROFIT.requestBodyConverter(type, NO_ANNOTATIONS, NO_ANNOTATIONS);
		List<Payload> payloads = List.of(new Payload("solar", 3));
		RequestBody request = requireNonNull(requestConverter.convert(payloads));
		Buffer buffer = new Buffer();
		request.writeTo(buffer);
		String json = buffer.readUtf8();
		assertEquals("application/json; charset=UTF-8", requireNonNull(request.contentType()).toString());
		Converter<ResponseBody, List<Payload>> responseConverter = RETROFIT.responseBodyConverter(type, NO_ANNOTATIONS);
		assertEquals(payloads, responseConverter.convert(ResponseBody.create(json, MediaType.get("application/json"))));
	}

	@Test
	void reportsMalformedResponseAsIOException() {
		Converter<ResponseBody, Payload> converter = RETROFIT.responseBodyConverter(Payload.class, NO_ANNOTATIONS);
		IOException exception = assertThrows(IOException.class, () -> converter.convert(ResponseBody.create("{", MediaType.get("application/json"))));
		assertInstanceOf(JacksonException.class, exception.getCause());
	}
}

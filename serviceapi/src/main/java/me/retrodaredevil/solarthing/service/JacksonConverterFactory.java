package me.retrodaredevil.solarthing.service;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.jspecify.annotations.NullMarked;
import retrofit2.Converter;
import retrofit2.Retrofit;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.ObjectWriter;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/** JSON conversion for Retrofit using Jackson 3. */
@NullMarked
public final class JacksonConverterFactory extends Converter.Factory {
	private static final MediaType JSON = MediaType.get("application/json; charset=UTF-8");
	private final ObjectMapper mapper;

	private JacksonConverterFactory(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	public static JacksonConverterFactory create(ObjectMapper mapper) {
		return new JacksonConverterFactory(mapper);
	}

	@Override
	public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
		ObjectReader reader = mapper.readerFor(mapper.constructType(type));
		return body -> {
			try (body) {
				return reader.readValue(body.byteStream());
			} catch (JacksonException e) {
				throw new IOException("Could not deserialize JSON response", e);
			}
		};
	}

	@Override
	public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
		ObjectWriter writer = mapper.writerFor(mapper.constructType(type));
		return (Converter<Object, RequestBody>) value -> {
			try {
				return RequestBody.create(writer.writeValueAsBytes(value), JSON);
			} catch (JacksonException e) {
				throw new IOException("Could not serialize JSON request", e);
			}
		};
	}
}
